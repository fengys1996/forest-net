package com.fnet.common.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class TcpServer {

    /**
     * eventLoopGroup in inner Server
      */
    public static EventLoopGroup CONNECT_REAL_SERVER_EVENTLOOP_GROUP = new NioEventLoopGroup(4, new DefaultThreadFactory("connect_real_server_eventloop_group"));
    public static EventLoopGroup CONNECT_OUTER_SERVER_EVENTLOOP_GROUP = new NioEventLoopGroup(4, new DefaultThreadFactory("connect_outer_server_eventloop_group"));

    /**
     * eventLoopGroup in outer server
     */
    public static EventLoopGroup MONITOR_BROWSER_BOSS_EVENTLOOP_GROUP = new NioEventLoopGroup(1, new DefaultThreadFactory("monitor_browser_boss_eventloop_group"));
    public static EventLoopGroup MONITOR_BROWSER_WORK_EVENTLOOP_GROUP = new NioEventLoopGroup(8, new DefaultThreadFactory("monitor_browser_worker_eventloop_group"));

    public static EventLoopGroup MONITOR_INNER_SERVER_BOSS_EVENTLOOP_GROUP = new NioEventLoopGroup(1, new DefaultThreadFactory("monitor_inner_server_boss_eventloop_group"));
    public static EventLoopGroup MONITOR_INNER_SERVER_WORK_EVENTLOOP_GROUP = new NioEventLoopGroup(8, new DefaultThreadFactory("monitor_inner_server_worker_eventloop_group"));



    public void startMonitor(int port, ChannelInitializer<SocketChannel> channelInitializer, EventLoopGroup bossGroup, EventLoopGroup workGroup) throws InterruptedException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(channelInitializer);
            ChannelFuture channelFuture = bootstrap.bind().sync();
            log.info("listen port: {}", port);
            channelFuture.channel().closeFuture().sync();
        }
        finally {
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }

    public void startConnect(String host, int port, EventLoopGroup eventLoopGroup, ChannelInitializer<SocketChannel> channelInitializer, int tcpNumber) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(channelInitializer);
        for (int i = 0; i < tcpNumber; i++) {
            bootstrap.connect(host, port).sync();
        }
    }

    /**
     * can be override by subclass
     */
    public void doSomeThingAfterConnectSuccess(Channel channel) {

    }

    public void startConnect1(String host, int port, EventLoopGroup eventLoopGroup, ChannelInitializer<SocketChannel> channelInitializer, int tcpNumber) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(channelInitializer);
        for (int i = 0; i < tcpNumber; i++) {
            ChannelFuture channelFuture;
            channelFuture = bootstrap.connect(host, port).sync();
            if (channelFuture.isSuccess()) {
                if (channelFuture.channel().isOpen()) {
                    doSomeThingAfterConnectSuccess(channelFuture.channel());
                }
            }
        }
    }
}
