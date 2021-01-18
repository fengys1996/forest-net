package com.fnet.common.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public interface NetService {

    void startMonitor(int port, ChannelInitializer<SocketChannel> channelInitializer, EventLoopGroup bossGroup, EventLoopGroup workGroup) throws Exception;

    Channel startConnect(String host, int port, ChannelInitializer<SocketChannel> channelInitializer, EventLoopGroup workGroup) throws Exception;
}
