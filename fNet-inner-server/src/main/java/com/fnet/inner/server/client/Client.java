package com.fnet.inner.server.client;

import com.fnet.common.codec.MessageDecoder;
import com.fnet.common.codec.MessageEncoder;
import com.fnet.common.codec.MyLengthFieldBasedFrameDecoder;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.inner.server.handler.KeepAliveHandler;
import com.fnet.inner.server.handler.MonitorOuterServerHandler;
import com.fnet.inner.server.handler.RegisterHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.channel.ChannelOption.WRITE_BUFFER_WATER_MARK;

@Slf4j
public class Client {
    private final int port;
    private final String host;
    private final ChannelInitializer<SocketChannel> channelInitializer;
    private final EventLoopGroup workGroup;
    private int lowWriteBufferWaterMark;
    private int highWriteBufferWaterMark;
    private final AtomicInteger retryTimes;

    public Client(int port, String host, Sender sender, String pwd, EventLoopGroup workGroup, MessageResolver messageResolver) {
        this.port = port;
        this.host = host;
        this.workGroup = workGroup;
        this.retryTimes = new AtomicInteger(0);

        RegisterHandler registerHandler = new RegisterHandler(sender, pwd);
        KeepAliveHandler keepAliveHandler = new KeepAliveHandler(sender);
        MonitorOuterServerHandler monitorOuterServerHandler = new MonitorOuterServerHandler(sender, messageResolver);
        this.channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("idleCheckHandler", new IdleStateHandler(0, 5, 0));
                pipeline.addLast("myLengthFieldBasedFrameDecoder", new MyLengthFieldBasedFrameDecoder());
                pipeline.addLast("messageEncoder", new MessageEncoder());
                pipeline.addLast("messageDecoder", new MessageDecoder());
                pipeline.addLast("registerHandler", registerHandler);
                pipeline.addLast("keepAliveHandler", keepAliveHandler);
                pipeline.addLast("monitorOuterServerHandler", monitorOuterServerHandler);
            }
        };
    }

    public void connectWithRetry() {
        int low = lowWriteBufferWaterMark == 0 ? 2 * 1024 * 1024 : lowWriteBufferWaterMark;
        int high = highWriteBufferWaterMark == 0 ? 4 * 1024 * 1024 : highWriteBufferWaterMark;
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(EpollSocketChannel.class)
                .option(WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(low, high))
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(channelInitializer);
        bootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                future.channel().closeFuture().addListener(closeFuture -> {
                    if (closeFuture.isSuccess()) reconnect();
                });
            } else reconnect();
        });
    }

    private void reconnect() throws InterruptedException {
        if (retryTimes.getAndIncrement() >= 20)  {
            log.warn("stop reconnect!");
            return;
        }
        log.warn("connect failed, reconnect later!");
        Thread.sleep(3000);
        connectWithRetry();
    }

    public void setLowWriteBufferWaterMark(int lowWriteBufferWaterMark) {
        this.lowWriteBufferWaterMark = lowWriteBufferWaterMark;
    }

    public void setHighWriteBufferWaterMark(int highWriteBufferWaterMark) {
        this.highWriteBufferWaterMark = highWriteBufferWaterMark;
    }
}
