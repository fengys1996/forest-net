package com.fnet.out.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.config.Config;
import com.fnet.common.config.cmd.CmdConfigService;
import com.fnet.common.net.TcpServer;
import com.fnet.out.server.handler.AuthHandler;
import com.fnet.out.server.handler.MonitorInnerServerHandler;
import com.fnet.out.server.handler.OuterServerIdleCheckHandler;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.cli.*;

import static com.fnet.common.net.TcpServer.*;

/**
 * start outer server here
 */
public class OuterServerApp {

    public static void main(String[] args) throws InterruptedException, ParseException {

        new CmdConfigService().setOuterServerConfig(args);

        if (Config.isOuterServerConfigComplete()) {
            new TcpServer().startMonitor(Config.OUTER_SERVER_PORT, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("messageEncoder", new MessageEncoder());
                    pipeline.addLast("messageDecoder", new MessageDecoder());
                    pipeline.addLast("authHandler", new AuthHandler());
                    pipeline.addLast("idleCheckHandler", new OuterServerIdleCheckHandler());
                    pipeline.addLast("monitorInnerServerHandler", new MonitorInnerServerHandler());
                }
            }, MONITOR_INNER_SERVER_BOSS_EVENTLOOP_GROUP, MONITOR_INNER_SERVER_WORK_EVENTLOOP_GROUP);
        }
    }
}
