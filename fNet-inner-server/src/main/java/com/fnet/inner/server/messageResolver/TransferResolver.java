package com.fnet.inner.server.messageResolver;

import com.fnet.common.net.TcpServer;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.inner.server.service.ContactOfOuterToInnerChannel;
import com.fnet.inner.server.service.InnerSender;
import com.fnet.inner.server.handler.MonitorRealServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class TransferResolver implements MessageResolver {

    @Override
    public void resolve(Message message) throws InterruptedException {
        int outerChannelId;
        Channel innerChannel;

        synchronized (TransferResolver.class) {
            outerChannelId = message.getOuterChannelId();
            innerChannel = ContactOfOuterToInnerChannel.getInstance().getInnerChannel(outerChannelId);

            if (innerChannel != null) {
                InnerSender.getInstance().sendBytesToRealServer(message);
            } else {
                new TcpServer(){
                    @Override
                    public void doSomeThingAfterConnectSuccess(Channel channel) {
                        System.out.println("connect success do after!");
                        ContactOfOuterToInnerChannel.getInstance().addToMap(message.getOuterChannelId(), channel);
                        InnerSender.getInstance().sendBytesToRealServer(channel, message);
                    }
                }.startConnect("127.0.0.1", 8080, new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), new MonitorRealServerHandler(message));
                    }
                });
            }
        }
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
