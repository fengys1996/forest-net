package com.fnet.inner.server.data;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class Sender {

    public static void sendBytesToBrowser(Message message) {
        Channel innerChannel = ContactOfOuterToInnerChannel.getInstance().getInnerChannel(message.getOuterChannelId());
        // inner channel must be not null
        innerChannel.writeAndFlush(Unpooled.copiedBuffer(message.getData())).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {

                } else {

                }
            }
        });
    }

    public static void sendRegisterMessage(Channel channel) {
        channel.writeAndFlush(new Message(MessageType.REGISTER, 0, null));
    }
}
