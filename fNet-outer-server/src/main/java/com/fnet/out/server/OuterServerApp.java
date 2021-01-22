package com.fnet.out.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.codec.MyLengthFieldBasedFrameDecoder;
import com.fnet.common.config.Configurable;
import com.fnet.common.config.OuterCmdParser;
import com.fnet.common.config.OuterServerConfig;
import com.fnet.common.net.EpollTcpService;
import com.fnet.common.net.NetService;
import com.fnet.common.service.Sender;
import com.fnet.common.tool.NetTool;
import com.fnet.common.tool.ThreadPoolTool;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.out.server.domainCenter.DomainDataService;
import com.fnet.out.server.domainCenter.DomainInfo;
import com.fnet.out.server.handler.AuthHandler;
import com.fnet.out.server.handler.MonitorInnerServerHandler;
import com.fnet.out.server.handler.OuterServerIdleCheckHandler;
import com.fnet.common.authCenter.Authencator;
import com.fnet.out.server.task.MonitorBrowserTask;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * start outer server here
 */
@Slf4j
@Component
public class OuterServerApp implements Configurable<OuterServerConfig> {

    @Autowired
    Sender sender;

    @Autowired
    MessageResolver messageResolver;

    @Autowired
    Authencator authencator;

    @Autowired
    DomainDataService domainDataService;

    @Autowired
    NetService netService;

    private OuterServerConfig config;

    public static void main(String[] args)
            throws Exception {

        AnnotationConfigApplicationContext springCtx;
        OuterServerApp outerServerApp;

        springCtx = new AnnotationConfigApplicationContext("com.fnet.out.server", "com.fnet.common");
        outerServerApp = (OuterServerApp) springCtx.getBean("outerServerApp");

        if (!outerServerApp.initConfig(args))       return;
        if (!outerServerApp.domeSomeSettingsAfterInitConfig())       return;
        outerServerApp.start();
    }

    public void start() throws Exception {
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

        MonitorInnerServerHandler monitorInnerServerHandler = new MonitorInnerServerHandler(sender, messageResolver,
                                                                                            authencator, domainDataService);
        AuthHandler authHandler = new AuthHandler(sender, authencator, domainDataService);

        CompletableFuture.runAsync(new MonitorBrowserTask(sender, domainDataService, netService, config(), bossGroup, workGroup),
                                   ThreadPoolTool.getCommonExecutor());

        netService.startMonitor(config.getOspForInner(), new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("idleCheckHandler", new OuterServerIdleCheckHandler());
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

    @Override
    public OuterServerConfig config() {
        return config;
    }

    @Override
    public boolean initConfig(String[] args) {
        try {
            config = new OuterCmdParser(args).parse();
        } catch (ParseException e) {
            log.info("cmd parser fialed!");
        }
        if (config == null)      return false;
        return true;
    }

    @Override
    public boolean domeSomeSettingsAfterInitConfig() {
        // init domain info
        ObjectUtil.checkNotNull(config, "config");
        try {
            Map<String, DomainInfo> domainInfoMap = domainDataService.initData(config().getDomainNameList());
            if (domainInfoMap == null || domainInfoMap.isEmpty())   return false;
        } catch (Exception e) {
            log.info("init domain name list failed!");
            return false;
        }

        // set tcp service
        if (NetTool.isLinuxEnvironment())
                ((EpollTcpService)netService).setEnableSoResuePort(config.getEnableSoReusePort());

        // set password
        authencator.setPassword(config.getPwd());
        return true;
    }
}
