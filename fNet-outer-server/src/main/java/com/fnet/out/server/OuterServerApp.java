package com.fnet.out.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.config.Config;
import com.fnet.common.config.cmd.CmdConfigService;
import com.fnet.common.net.TcpServer;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.out.server.handler.AuthHandler;
import com.fnet.out.server.handler.MonitorInnerServerHandler;
import com.fnet.out.server.handler.OuterServerIdleCheckHandler;
import com.fnet.out.server.service.AuthService;
import com.fnet.out.server.service.OuterChannelDataService;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

import static com.fnet.common.net.TcpServer.*;

/**
 * start outer server here
 */
@Component
public class OuterServerApp {

    @Autowired
    Sender sender;

    @Autowired
    MessageResolver messageResolver;

    @Autowired
    OuterChannelDataService outerChannelDataService;

    @Autowired
    AuthService authService;

    public void start() throws ParseException, InterruptedException, CertificateException, SSLException {

        if (Config.isOuterServerConfigComplete()) {
            MonitorInnerServerHandler monitorInnerServerHandler;
            AuthHandler authHandler;

            monitorInnerServerHandler = new MonitorInnerServerHandler(sender, messageResolver, authService, outerChannelDataService);
            authHandler = new AuthHandler(sender, authService);
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            SslContext sslContext =
                    SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey())
                                     .build();
            System.out.println(selfSignedCertificate.certificate());

            new TcpServer().startMonitor(Config.OUTER_SERVER_PORT, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("idleCheckHandler", new OuterServerIdleCheckHandler());
                    pipeline.addLast("sslHandler", sslContext.newHandler(ch.alloc()));
                    pipeline.addLast("messageEncoder", new MessageEncoder());
                    pipeline.addLast("messageDecoder", new MessageDecoder());
                    pipeline.addLast("authHandler", authHandler);
                    pipeline.addLast("monitorInnerServerHandler", monitorInnerServerHandler);
                }
            }, MONITOR_INNER_SERVER_BOSS_EVENTLOOP_GROUP, MONITOR_INNER_SERVER_WORK_EVENTLOOP_GROUP);
        }
    }

    public static void main(String[] args)
            throws InterruptedException, ParseException, CertificateException, SSLException {

        new CmdConfigService().setOuterServerConfig(args);

        AnnotationConfigApplicationContext springCtx;
        OuterServerApp outerServerApp;

        springCtx = new AnnotationConfigApplicationContext("com.fnet.out.server");
        outerServerApp = (OuterServerApp) springCtx.getBean("outerServerApp");

        outerServerApp.start();
    }
}
