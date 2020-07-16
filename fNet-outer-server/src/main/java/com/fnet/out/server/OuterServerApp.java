package com.fnet.out.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.config.Config;
import com.fnet.common.net.TcpServer;
import com.fnet.out.server.handler.MonitorInnerServerHandler;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.cli.*;

/**
 * start outer server here
 */
public class OuterServerApp {

    public static void main(String[] args) throws InterruptedException, ParseException {

        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("port", true, "fNet Server port");
        options.addOption("password", true, "fNet Server password");
        options.addOption("remotePort", true, "Outer server port for monitor browser!");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {
            Config.OUTER_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("port", String.valueOf(Config.DEFAULT_OUTER_SERVER_PORT)));
            Config.PASSWORD = commandLine.getOptionValue("password", Config.DEFAULT_PASSWORD);

            new TcpServer().startMonitor(Config.OUTER_SERVER_PORT, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new MessageDecoder(), new MessageEncoder(), new IdleStateHandler(30, 30, 30),
                        new MonitorInnerServerHandler());
                }
            });
        }
    }
}
