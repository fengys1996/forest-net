package com.fnet.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.net.TcpServer;
import com.fnet.server.handler.MonitorInnerServerHandler;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

/**
 * start outer server here
 */
public class ServerApp {

    public static void main(String[] args) throws InterruptedException {
         new TcpServer().startMonitor(9090, new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline pipeline = ch.pipeline();
                 pipeline.addLast(new MessageDecoder(), new MessageEncoder(), new MonitorInnerServerHandler());
             }
         });
    }
}
