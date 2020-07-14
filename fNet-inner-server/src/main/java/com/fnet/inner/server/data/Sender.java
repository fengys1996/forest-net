package com.fnet.inner.server.data;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class Sender {

    public static void sendBytesToRealServer(Message message) {
        Channel innerChannel = ContactOfOuterToInnerChannel.getInstance().getInnerChannel(message.getOuterChannelId());
        if (innerChannel == null) {
            System.out.println("Have not inner channel to send bytes to real server!");
        } else {
            innerChannel.writeAndFlush(message.getData()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {

                    } else {

                    }
                }
            });
        }
    }

    public static void sendBytesToRealServer(Channel channel, Message message) {
        if (channel == null) {
            System.out.println("Have not inner channel to send bytes to real server!");
        } else if(channel.isOpen()) {
            channel.writeAndFlush(message.getData()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {

                    } else {

                    }
                }
            });
        }
    }

    public static void sendMessageToOuterServer(Message message) {
        Channel readyTransferChannel = TransferChannelData.getInstance().getReadyTransferChannel();
        readyTransferChannel.writeAndFlush(message).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                }
            }
        });
    }

    public static void sendRegisterMessage(Channel channel) {
        channel.writeAndFlush(new Message(MessageType.REGISTER, 0, null));
    }
}
