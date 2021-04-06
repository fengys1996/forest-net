package com.fnet.inner.server.messageQueue;

import com.fnet.common.net.NetService;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.inner.server.handler.MonitorRealServerHandler;
import com.fnet.inner.server.sender.TransferCache;
import com.lmax.disruptor.EventHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.net.ConnectException;

public class MessageEventHandler implements EventHandler<MessageEvent> {

    Sender sender;
    EventLoopGroup workGroup;
    NetService netService;

    String realServerAddr;
    int realServerPort;

    public MessageEventHandler(Sender sender, NetService netService, EventLoopGroup workGroup, String realServerAddr, int realServerPort) {
        this.sender = sender;
        this.workGroup = workGroup;
        this.netService = netService;
        this.realServerAddr = realServerAddr;
        this.realServerPort = realServerPort;
    }

    @Override
    public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) throws Exception {
        Message message = event.getMessage();

        int outerChannelId = message.getOuterChannelId();
        Channel innerChannel = TransferCache.getInnerChannel(outerChannelId);
        if (innerChannel != null) {
            sender.sendBytesToRealServer(message);
        } else {
            try {
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
            } catch (ConnectException e) {
                e.printStackTrace();
            }

        }
    }
}
