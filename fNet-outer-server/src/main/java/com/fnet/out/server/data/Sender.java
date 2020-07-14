package com.fnet.out.server.data;

import com.fnet.common.transferProtocol.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.TimeUnit;

public class Sender {

    public static void sendMessageToTransferChannel(Message message) {
        System.out.println("sendMessageToTransferChannel");
        Channel readyChannel;
        readyChannel = TransferChannelData.getInstance().getReadyTransferChannel();
        while (readyChannel == null) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            readyChannel = TransferChannelData.getInstance().getReadyTransferChannel();
        }

        Channel finalChannel = readyChannel;
        System.out.println("hash code=" + finalChannel.hashCode());
        readyChannel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                TransferChannelData.getInstance().freeChannel(finalChannel);
                System.out.println("send message success!(outer server to transfer readyChannel)");
            } else {
                System.out.println("send message failed!(outer server to transfer readyChannel)");
            }
        });
        System.out.println("sendMessageToTransferChannel over");
    }

    public static void sendBytesToBrowser(Message message) {
        Channel outerChannel;
        outerChannel = OuterChannelData.getInstance().getOuterChannelById(message.getOuterChannelId());

        if (outerChannel != null && outerChannel.isOpen()) {
            outerChannel.writeAndFlush(message.getData()).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    System.out.println("send to browser success!!!");
                } else {
                    System.out.println("send to browser failed!!!");
                }
            });
        } else {
            System.out.println("have no channel to Browser!!!");
        }
    }
}
