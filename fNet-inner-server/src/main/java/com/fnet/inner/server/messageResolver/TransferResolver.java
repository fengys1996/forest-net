package com.fnet.inner.server.messageResolver;

import com.fnet.common.net.TcpServer;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.inner.server.data.ContactOfOuterToInnerChannel;
import com.fnet.inner.server.data.Sender;
import com.fnet.inner.server.handler.MonitorRealServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class TransferResolver implements MessageResolver {

    @Override
    public void resolve(Message message) throws InterruptedException {

        int outerChannelId;
        Channel innerChannel;

        outerChannelId = message.getOuterChannelId();
        innerChannel = ContactOfOuterToInnerChannel.getInstance().getInnerChannel(outerChannelId);

        if (innerChannel != null) {
            System.out.println(new String(message.getData()));
//            Sender.sendBytesToBrowser(message);
        } else {
            new TcpServer().startConnect("127.0.0.1", 8080, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), new MonitorRealServerHandler(message));
                }
            });
        }
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
