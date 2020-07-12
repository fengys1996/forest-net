package com.fnet.common.net;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class TcpServer {

    public void startMonitor(int port, ChannelInitializer<SocketChannel> channelInitializer) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .localAddress(new InetSocketAddress(port))
//                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(channelInitializer);
            System.out.println("start Monitor Transfer Server!!!");
            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        }
        finally {
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }
}
