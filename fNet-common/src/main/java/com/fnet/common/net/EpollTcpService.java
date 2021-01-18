package com.fnet.common.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

import static io.netty.channel.ChannelOption.*;

@Slf4j
@Conditional(LinuxCondition.class)
@Component
public class EpollTcpService implements NetService {

    private static final WriteBufferWaterMark
            WRITE_BUFFER_WATER_MARK_OF_OUTER_SERVER = new WriteBufferWaterMark(2 * 1024 * 1024, 4 * 1024 * 1024);
    private static final WriteBufferWaterMark
            WRITE_BUFFER_WATER_MARK_OF_INNER_SERVER = new WriteBufferWaterMark(2 * 1024 * 1024, 4 * 1024 * 1024);

    @Override
    public void startMonitor(int port, ChannelInitializer<SocketChannel> channelInitializer, EventLoopGroup bossGroup, EventLoopGroup workGroup) throws InterruptedException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                     .channel(EpollServerSocketChannel.class)
                     .option(ChannelOption.SO_BACKLOG, 128)
                     .option(EpollChannelOption.SO_REUSEPORT, true)
                     .childOption(ChannelOption.SO_KEEPALIVE, true)
                     .childOption(WRITE_BUFFER_WATER_MARK, WRITE_BUFFER_WATER_MARK_OF_OUTER_SERVER)
                     .localAddress(new InetSocketAddress(port))
                     .childHandler(channelInitializer);
            for (int i = 0 ; i < NettyRuntime.availableProcessors() ; i++) {
                ChannelFuture channelFuture = bootstrap.bind().sync();
                channelFuture.channel().closeFuture().sync();
            }
            log.info("listen port: {}", port);
        }
        finally {
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }

    @Override
    public Channel startConnect(String host, int port, ChannelInitializer<SocketChannel> channelInitializer,
                                EventLoopGroup workGroup) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                 .channel(EpollServerSocketChannel.class)
                 .option(WRITE_BUFFER_WATER_MARK, WRITE_BUFFER_WATER_MARK_OF_INNER_SERVER)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(channelInitializer);
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        return channelFuture.channel();
    }
}
