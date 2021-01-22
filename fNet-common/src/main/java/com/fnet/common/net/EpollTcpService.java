package com.fnet.common.net;

import com.fnet.common.config.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.unix.UnixChannelOption;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelOption.*;

@Slf4j
@Conditional(LinuxCondition.class)
@Component
public class EpollTcpService implements NetService {

    private static final WriteBufferWaterMark
            WRITE_BUFFER_WATER_MARK_OF_OUTER_SERVER = new WriteBufferWaterMark(2 * 1024 * 1024, 4 * 1024 * 1024);
    private static final WriteBufferWaterMark
            WRITE_BUFFER_WATER_MARK_OF_INNER_SERVER = new WriteBufferWaterMark(2 * 1024 * 1024, 4 * 1024 * 1024);

    private int enableSoResuePort;

    public void setEnableSoResuePort(int enableSoResuePort) {
        this.enableSoResuePort = enableSoResuePort;
    }

    @Override
    public void startMonitor(int port, ChannelInitializer<SocketChannel> channelInitializer, EventLoopGroup bossGroup, EventLoopGroup workGroup) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                 .channel(EpollServerSocketChannel.class)
                 .option(ChannelOption.SO_BACKLOG, 128)
                 .childOption(ChannelOption.SO_KEEPALIVE, true)
                 .childOption(WRITE_BUFFER_WATER_MARK, WRITE_BUFFER_WATER_MARK_OF_OUTER_SERVER)
                 .localAddress(new InetSocketAddress(port))
                 .childHandler(channelInitializer);

        int listenSocketNum = 1;
        if (enableSoResuePort == 1) {
            log.info("Enable so_resueport!");
            bootstrap.option(UnixChannelOption.SO_REUSEPORT, true);
            listenSocketNum = NettyRuntime.availableProcessors();
        }

        CountDownLatch countDownLatch = new CountDownLatch(listenSocketNum);

        for (int i = 0; i < listenSocketNum; i++) {
            bootstrap.bind().addListener(o->{
                if (o.isSuccess()) {
                    countDownLatch.countDown();
                }
            });
        }
        if (countDownLatch.await(2000, TimeUnit.MILLISECONDS)) {
            log.info("listen port: {}", port);
        }
    }

    @Override
    public Channel startConnect(String host, int port, ChannelInitializer<SocketChannel> channelInitializer,
                                EventLoopGroup workGroup) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                 .channel(EpollSocketChannel.class)
                 .option(WRITE_BUFFER_WATER_MARK, WRITE_BUFFER_WATER_MARK_OF_INNER_SERVER)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(channelInitializer);
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        return channelFuture.channel();
    }
}
