package com.fnet.inner.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.config.Config;
import com.fnet.common.config.cmd.CmdConfigService;
import com.fnet.common.net.TcpServer;
import com.fnet.common.service.Sender;
import com.fnet.common.service.ThreadPoolUtil;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.inner.server.handler.KeepAliveHandler;
import com.fnet.inner.server.handler.MonitorOuterServerHandler;
import com.fnet.inner.server.handler.RegisterHandler;
import com.fnet.inner.server.service.Outer2InnerInfoService;
import com.fnet.inner.server.task.SendMessageToRealServerTask;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.fnet.common.net.TcpServer.*;

@Component
public class InnerServerApp {

    @Autowired
    Sender sender;

    @Autowired
    MessageResolver resolver;

    @Autowired
    Outer2InnerInfoService outer2InnerInfoService;

    public void start() throws ParseException, InterruptedException {

        if (Config.isInnerServerConfigComplete()) {

            CompletableFuture.runAsync(new SendMessageToRealServerTask(sender, outer2InnerInfoService), ThreadPoolUtil.getCommonExecutor());

            RegisterHandler registerHandler;
            KeepAliveHandler keepAliveHandler;
            MonitorOuterServerHandler monitorOuterServerHandler;

            registerHandler = new RegisterHandler(sender);
            keepAliveHandler = new KeepAliveHandler(sender);
            monitorOuterServerHandler = new MonitorOuterServerHandler(sender, resolver);

            // create a channel to register, when register success, then create other channels
            new TcpServer().startConnect(Config.OUTER_SERVER_ADDRESS , Config.OUTER_SERVER_PORT, CONNECT_OUTER_SERVER_EVENTLOOP_GROUP, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("messageEncoder", new MessageEncoder());
                    pipeline.addLast("messageDecoder", new MessageDecoder());
                    pipeline.addLast("idleCheckHandler",  new IdleStateHandler(0, 5, 0));
                    pipeline.addLast("registerHandler", registerHandler);
                    pipeline.addLast("keepAliveHandler", keepAliveHandler);
                    pipeline.addLast("monitorOuterServerHandler", monitorOuterServerHandler);
                }
            }, Config.TRANSFER_CHANNEL_NUMBERS);
        }
    }

    public static void main(String[] args) throws InterruptedException, ParseException {

        new CmdConfigService().setInnerServerConfig(args);

        AnnotationConfigApplicationContext springCtx;
        InnerServerApp innerServerApp;

        springCtx = new AnnotationConfigApplicationContext("com.fnet.inner.server");
        innerServerApp = (InnerServerApp) springCtx.getBean("innerServerApp");

        innerServerApp.start();
    }
}
