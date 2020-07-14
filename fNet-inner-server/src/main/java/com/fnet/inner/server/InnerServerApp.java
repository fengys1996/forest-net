package com.fnet.inner.server;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.net.TcpServer;
import com.fnet.inner.server.handler.MonitorOuterServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class InnerServerApp {

    public static void main(String[] args) throws InterruptedException {
        new TcpServer().startConnect("127.0.0.1", 9091, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new MessageDecoder(), new MessageEncoder(), new MonitorOuterServerHandler());
            }
        });
    }
}
