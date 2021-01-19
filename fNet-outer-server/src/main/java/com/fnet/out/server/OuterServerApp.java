package com.fnet.out.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.codec.MyLengthFieldBasedFrameDecoder;
import com.fnet.common.config.Config;
import com.fnet.common.config.cmd.CmdConfigService;
import com.fnet.common.net.NetService;
import com.fnet.common.service.Sender;
import com.fnet.common.tool.NetTool;
import com.fnet.common.tool.ThreadPoolTool;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.out.server.domainCenter.DomainDataService;
import com.fnet.out.server.handler.AuthHandler;
import com.fnet.out.server.handler.MonitorInnerServerHandler;
import com.fnet.out.server.handler.OuterServerIdleCheckHandler;
import com.fnet.out.server.authCenter.AuthService;
import com.fnet.out.server.task.MonitorBrowserTask;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * start outer server here
 */
@Slf4j
@Component
public class OuterServerApp {

    @Autowired
    Sender sender;

    @Autowired
    MessageResolver messageResolver;

    @Autowired
    AuthService authService;

    @Autowired
    DomainDataService domainDataService;

    @Autowired
    NetService netService;

    public void start() throws Exception {

        if (Config.isOuterServerConfigComplete()) {

            // create boss and work event loop group
            int availableProcessors = NettyRuntime.availableProcessors();

            EventLoopGroup bossGroup;
            EventLoopGroup workGroup;
            String bossThreadPoolName = "outer_server_boss_group";
            String workThreadPoolName = "outer_server_work_group";
            if (!NetTool.isLinuxEnvironment()) {
                bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory(bossThreadPoolName));
                workGroup = new NioEventLoopGroup(availableProcessors, new DefaultThreadFactory(workThreadPoolName));
            } else {
                bossGroup = new EpollEventLoopGroup(availableProcessors, new DefaultThreadFactory(bossThreadPoolName));
                workGroup = new EpollEventLoopGroup(availableProcessors, new DefaultThreadFactory(workThreadPoolName));
            }
            MonitorInnerServerHandler monitorInnerServerHandler = new MonitorInnerServerHandler(sender, messageResolver, authService, domainDataService);
            AuthHandler authHandler = new AuthHandler(sender, authService, domainDataService);

            // Formal environments require trusted certificates, not selfSignedCertificate!!!
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            SslContext sslContext =
                    SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey())
                                     .build();
            System.out.println(selfSignedCertificate.certificate());

            CompletableFuture.runAsync(new MonitorBrowserTask(sender, domainDataService, netService, bossGroup, workGroup),
                                       ThreadPoolTool.getCommonExecutor());

            netService.startMonitor(Config.OUTER_SERVER_PORT, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("idleCheckHandler", new OuterServerIdleCheckHandler());
//                    pipeline.addLast("sslHandler", sslContext.newHandler(ch.alloc()));
                    pipeline.addLast("myLengthFieldBasedFrameDecoder", new MyLengthFieldBasedFrameDecoder());
                    pipeline.addLast("messageEncoder", new MessageEncoder());
                    pipeline.addLast("messageDecoder", new MessageDecoder());
                    pipeline.addLast("authHandler", authHandler);
                    pipeline.addLast("monitorInnerServerHandler", monitorInnerServerHandler);
                }
            }, bossGroup, workGroup);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("start shutdown hook!");
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
                ThreadPoolTool.getCommonExecutor().shutdownNow();
            }));
        }
    }

    public static void main(String[] args)
            throws Exception {

        new CmdConfigService().setOuterServerConfig(args);

        AnnotationConfigApplicationContext springCtx;
        OuterServerApp outerServerApp;

        springCtx = new AnnotationConfigApplicationContext("com.fnet.out.server", "com.fnet.common");
        outerServerApp = (OuterServerApp) springCtx.getBean("outerServerApp");

        outerServerApp.start();
    }
}
