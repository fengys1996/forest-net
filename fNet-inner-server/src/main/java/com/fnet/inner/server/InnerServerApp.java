package com.fnet.inner.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.config.Config;
import com.fnet.common.config.cmd.CmdConfigService;
import com.fnet.common.net.NetService;
import com.fnet.common.service.Sender;
import com.fnet.common.tool.NetTool;
import com.fnet.common.tool.ThreadPoolTool;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.inner.server.handler.KeepAliveHandler;
import com.fnet.inner.server.handler.MonitorOuterServerHandler;
import com.fnet.inner.server.handler.RegisterHandler;
import com.fnet.inner.server.task.SendMessageToRealServerTask;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class InnerServerApp {

    @Autowired
    Sender sender;

    @Autowired
    MessageResolver resolver;

    @Autowired
    NetService netService;

    public void start() throws Exception {

        if (Config.isInnerServerConfigComplete()) {

            int availableProcessors = NettyRuntime.availableProcessors();
            EventLoopGroup workGroup;
            if (!NetTool.isLinuxEnvironment()) {
                workGroup = new NioEventLoopGroup(availableProcessors, new DefaultThreadFactory("inner_server_work_group"));
            } else {
                workGroup = new EpollEventLoopGroup(availableProcessors, new DefaultThreadFactory("inner_server_work_group"));
            }

            CompletableFuture.runAsync(new SendMessageToRealServerTask(sender, netService, workGroup), ThreadPoolTool.getCommonExecutor());

            RegisterHandler registerHandler;
            KeepAliveHandler keepAliveHandler;
            MonitorOuterServerHandler monitorOuterServerHandler;
            SslContextBuilder sslContextBuilder;
            SslContext sslContext;

            registerHandler = new RegisterHandler(sender);
            keepAliveHandler = new KeepAliveHandler(sender);
            monitorOuterServerHandler = new MonitorOuterServerHandler(sender, resolver);
            sslContextBuilder = SslContextBuilder.forClient()
                                                 .trustManager(InsecureTrustManagerFactory.INSTANCE); // The formal environment needs to delete this line of code!!!
            sslContext = sslContextBuilder.build();

            // create a channel to register, when register success, then create other channels
            netService.startConnect(Config.OUTER_SERVER_ADDRESS , Config.OUTER_SERVER_PORT, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("idleCheckHandler",  new IdleStateHandler(0, 5, 0));
//                    pipeline.addLast("sslHandler", sslContext.newHandler(ch.alloc()));
                    pipeline.addLast("messageEncoder", new MessageEncoder());
                    pipeline.addLast("messageDecoder", new MessageDecoder());
                    pipeline.addLast("registerHandler", registerHandler);
                    pipeline.addLast("keepAliveHandler", keepAliveHandler);
                    pipeline.addLast("monitorOuterServerHandler", monitorOuterServerHandler);
                }
            }, workGroup);
        }
    }

    public static void main(String[] args) throws Exception {

        new CmdConfigService().setInnerServerConfig(args);

        AnnotationConfigApplicationContext springCtx;
        InnerServerApp innerServerApp;

        springCtx = new AnnotationConfigApplicationContext("com.fnet.inner.server", "com.fnet.common");
        innerServerApp = (InnerServerApp) springCtx.getBean("innerServerApp");

        innerServerApp.start();
    }
}
