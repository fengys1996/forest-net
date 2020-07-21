package com.fnet.inner.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.config.Config;
import com.fnet.common.config.cmd.CmdConfigService;
import com.fnet.common.net.TcpServer;
import com.fnet.inner.server.handler.MonitorOuterServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.cli.*;

public class InnerServerApp {

    public static void main(String[] args) throws InterruptedException, ParseException {

        new CmdConfigService().setInnerServerConfig(args);

        if (Config.isInnerServerConfigComplete()) {
            new TcpServer().startConnect(Config.OUTER_SERVER_ADDRESS , Config.OUTER_SERVER_PORT, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MessageDecoder(), new MessageEncoder(),
                            new IdleStateHandler(30, 30, 30), new MonitorOuterServerHandler());
                }
            });
        }
    }
}
