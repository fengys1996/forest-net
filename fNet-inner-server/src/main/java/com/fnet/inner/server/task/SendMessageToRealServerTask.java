package com.fnet.inner.server.task;

import com.fnet.common.net.NetService;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.inner.server.handler.MonitorRealServerHandler;
import com.fnet.inner.server.messageResolver.TransferResolver;
import com.fnet.inner.server.sender.TransferCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.SneakyThrows;

import java.util.concurrent.LinkedBlockingQueue;

public class SendMessageToRealServerTask implements Runnable {

    LinkedBlockingQueue<Message> messageQueue = TransferResolver.MESSAGE_QUEUE;

    Sender sender;
    EventLoopGroup workGroup;
    NetService netService;

    String realServerAddr;
    int realServerPort;

    public SendMessageToRealServerTask(Sender sender, NetService netService, EventLoopGroup workGroup, String realServerAddr, int realServerPort) {
        this.sender = sender;
        this.workGroup = workGroup;
        this.netService = netService;
        this.realServerAddr = realServerAddr;
        this.realServerPort = realServerPort;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            Message message = messageQueue.take();
            int outerChannelId;
            Channel innerChannel;

            outerChannelId = message.getOuterChannelId();
            innerChannel = TransferCache.getInnerChannel(outerChannelId);

            if (innerChannel != null) {
                sender.sendBytesToRealServer(message);
            } else {
                Channel channel =
                        netService.startConnect(realServerAddr, realServerPort,
                                     new ChannelInitializer<SocketChannel>() {
                                       @Override
                                       protected void initChannel(SocketChannel ch)
                                               throws Exception {
                                           ch.pipeline().addLast(new MonitorRealServerHandler(message, sender));
                                       }
                                   }, workGroup);
                TransferCache.addToMap(message.getOuterChannelId(), channel);
                sender.sendBytesToRealServer(channel, message);
            }
        }
    }
}
