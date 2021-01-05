package com.fnet.inner.server.task;

import com.fnet.common.config.Config;
import com.fnet.common.net.TcpServer;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.inner.server.handler.MonitorRealServerHandler;
import com.fnet.inner.server.messageResolver.TransferResolver;
import com.fnet.inner.server.service.Outer2InnerInfoService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.SneakyThrows;

import java.util.concurrent.LinkedBlockingQueue;

import static com.fnet.common.net.TcpServer.*;

public class SendMessageToRealServerTask implements Runnable {

    LinkedBlockingQueue<Message> messageQueue = TransferResolver.MESSAGE_QUEUE;
    Sender sender;
    Outer2InnerInfoService outer2InnerInfoService;

    public SendMessageToRealServerTask(Sender sender, Outer2InnerInfoService outer2InnerInfoService) {
        this.sender = sender;
        this.outer2InnerInfoService = outer2InnerInfoService;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            Message message = messageQueue.take();
            int outerChannelId;
            Channel innerChannel;

            outerChannelId = message.getOuterChannelId();
            innerChannel = Outer2InnerInfoService.getInstance().getInnerChannel(outerChannelId);

            if (innerChannel != null) {
                sender.sendBytesToRealServer(message);
            } else {
                new TcpServer(){
                    @Override
                    public void doSomeThingAfterConnectSuccess(Channel channel) {
                        Outer2InnerInfoService.getInstance().addToMap(message.getOuterChannelId(), channel);
                        sender.sendBytesToRealServer(channel, message);
                    }
                }.startConnect1(Config.REAL_SERVER_ADDRESS, Config.REAL_SERVER_PORT, CONNECT_REAL_SERVER_EVENTLOOP_GROUP, new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayDecoder(),
                                              new ByteArrayEncoder(),
                                              new MonitorRealServerHandler(message, sender, outer2InnerInfoService));
                    }
                }, 1);
            }
        }
    }
}
