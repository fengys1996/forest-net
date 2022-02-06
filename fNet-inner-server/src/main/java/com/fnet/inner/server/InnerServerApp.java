package com.fnet.inner.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.codec.MyLengthFieldBasedFrameDecoder;
import com.fnet.common.config.Configurable;
import com.fnet.common.config.InnerCmdParser;
import com.fnet.common.config.InnerServerConfig;
import com.fnet.common.net.NetService;
import com.fnet.common.service.Sender;
import com.fnet.common.tool.NetTool;
import com.fnet.common.tool.ThreadPoolTool;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.inner.server.handler.KeepAliveHandler;
import com.fnet.inner.server.handler.MonitorOuterServerHandler;
import com.fnet.inner.server.handler.RegisterHandler;
import com.fnet.inner.server.messageQueue.MessageEvent;
import com.fnet.inner.server.messageQueue.MessageEventFactory;
import com.fnet.inner.server.messageQueue.MessageEventHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InnerServerApp implements Configurable<InnerServerConfig> {
    @Autowired
    Sender sender;

    @Autowired
    MessageResolver resolver;

    @Autowired
    NetService netService;

    InnerServerConfig config;
    public static final Disruptor<MessageEvent> DISRUPTOR = new Disruptor<MessageEvent>(new MessageEventFactory(),
                                                                                        1024 * 1024,
                                                                                        new DefaultThreadFactory("message_transfer"),
                                                                                        ProducerType.SINGLE,
                                                                                        new YieldingWaitStrategy());

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext springCtx;
        InnerServerApp innerServerApp;

        springCtx = new AnnotationConfigApplicationContext("com.fnet.inner.server", "com.fnet.common");
        innerServerApp = (InnerServerApp) springCtx.getBean("innerServerApp");

        if (!innerServerApp.initConfig(args))   return;
        innerServerApp.start();
    }

    private void start() throws Exception {
        // create eventloop group(worker)
        int availableProcessors = NettyRuntime.availableProcessors();
        EventLoopGroup workGroup;
        if (!NetTool.isLinuxEnvironment()) {
            workGroup = new NioEventLoopGroup(availableProcessors, new DefaultThreadFactory("inner_server_work_group"));
        } else {
            workGroup = new EpollEventLoopGroup(availableProcessors, new DefaultThreadFactory("inner_server_work_group"));
        }

        // regisiter event handler to carry data from disruptor queue to real server
        DISRUPTOR.handleEventsWith(new MessageEventHandler(sender, netService, workGroup, config().getRsa(), config().getRsp()));
        DISRUPTOR.start();

        // create some handler and start connect outer server
        RegisterHandler registerHandler = new RegisterHandler(sender, config().getPwd());
        KeepAliveHandler keepAliveHandler = new KeepAliveHandler(sender);
        MonitorOuterServerHandler monitorOuterServerHandler = new MonitorOuterServerHandler(sender, resolver);

        // create a channel to register, when register success, then server work
        netService.startConnect(config().getOsa() , config().getOspForInner(), new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("idleCheckHandler",  new IdleStateHandler(0, 5, 0));
                pipeline.addLast("myLengthFieldBasedFrameDecoder", new MyLengthFieldBasedFrameDecoder());
                pipeline.addLast("messageEncoder", new MessageEncoder());
                pipeline.addLast("messageDecoder", new MessageDecoder());
                pipeline.addLast("registerHandler", registerHandler);
                pipeline.addLast("keepAliveHandler", keepAliveHandler);
                pipeline.addLast("monitorOuterServerHandler", monitorOuterServerHandler);
            }
        }, workGroup);

        // clean resources before jvm shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("start shutdown hook!");
            workGroup.shutdownGracefully();
            ThreadPoolTool.getCommonExecutor().shutdownNow();
            DISRUPTOR.shutdown();
        }));
    }

    @Override
    public InnerServerConfig config() {
        return config;
    }

    @Override
    public boolean initConfig(String[] args) {
        try {
            config = new InnerCmdParser(args).parse();
        } catch (ParseException e) {
            log.info("cmd parser failed!");
        }
        if (config == null)      return false;
        return true;
    }

    @Override
    public boolean domeSomeSettingsAfterInitConfig() {
        // temporary do nothing
        return true;
    }
}
