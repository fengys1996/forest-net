package com.fnet.inner.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.config.Config;
import com.fnet.common.net.TcpServer;
import com.fnet.inner.server.handler.MonitorOuterServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.cli.*;

public class InnerServerApp {

    public static void main(String[] args) throws InterruptedException, ParseException {

        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("osa", true, "Outer server address!");
        options.addOption("osp", true, "Outer server port for monitor inner server!");

        options.addOption("rsp", true, "Real server port!");
        options.addOption("rsa", true, "Real server address!");

        options.addOption("pwd", true, "Password!");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {
            Config.OUTER_REMOTE_PORT = Integer.parseInt(commandLine.getOptionValue("p", String.valueOf(Config.DEFAULT_OUTER_REMOTE_PORT)));
            Config.OUTER_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("osp", String.valueOf(Config.DEFAULT_OUTER_SERVER_PORT)));
            Config.OUTER_SERVER_ADDRESS = commandLine.getOptionValue("osa", Config.DEFAULT_OUTER_SERVER_ADDRESS);

            Config.PASSWORD = commandLine.getOptionValue("pwd", Config.DEFAULT_PASSWORD);

            Config.REAL_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("rsp", String.valueOf(Config.DEFAULT_REAL_SERVER_PORT)));
            Config.REAL_SERVER_ADDRESS = commandLine.getOptionValue("rsa", Config.DEFAULT_REAL_SERVER_ADDRESS);

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
