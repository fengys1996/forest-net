package com.fnet.inner.server.messageResolver;

import com.fnet.common.config.Config;
import com.fnet.common.net.TcpServer;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.inner.server.service.ContactOfOuterToInnerChannel;
import com.fnet.inner.server.service.InnerSender;
import com.fnet.inner.server.handler.MonitorRealServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import static com.fnet.common.net.TcpServer.*;

public class TransferResolver implements MessageResolver {

    @Override
    public void resolve(Message message) throws InterruptedException {
        int outerChannelId;
        Channel innerChannel;

        outerChannelId = message.getOuterChannelId();
        innerChannel = ContactOfOuterToInnerChannel.getInstance().getInnerChannel(outerChannelId);

        if (innerChannel != null) {
            InnerSender.getInstance().sendBytesToRealServer(message);
        } else {
            new TcpServer(){
                @Override
                public void doSomeThingAfterConnectSuccess(Channel channel) {
                    ContactOfOuterToInnerChannel.getInstance().addToMap(message.getOuterChannelId(), channel);
                    InnerSender.getInstance().sendBytesToRealServer(channel, message);
                }
            }.startConnect1(Config.REAL_SERVER_ADDRESS, Config.REAL_SERVER_PORT, CONNECT_REAL_SERVER_EVENTLOOP_GROUP, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ByteArrayDecoder(),
                                          new ByteArrayEncoder(),
                                          new MonitorRealServerHandler(message, InnerSender.getInstance()));
                }
            }, 1);
        }
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
