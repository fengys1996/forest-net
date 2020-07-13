package com.fnet.out.server.data;

import com.fnet.common.transferProtocol.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.TimeUnit;

public class Sender {

    // 应该有一个重试次数限制，超出则放弃
    public static void sendMessageToTransferChannel(Message message) {
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
        readyChannel.writeAndFlush(message).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    TransferChannelData.getInstance().freeChannel(finalChannel);
//                    log.info("send message success!(outer server to transfer readyChannel)");
                } else {
//                    log.info("send message failed!(outer server to transfer readyChannel)");
                }
            }
        });
    }

    public static void sendBytesToBrowser(Message message) {
        Channel outerChannel;
        outerChannel = OuterChannelData.getInstance().getOuterChannelById(message.getOuterChannelId());

        if (outerChannel != null && outerChannel.isOpen()) {
            outerChannel.writeAndFlush(Unpooled.copiedBuffer(message.getData())).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
//                        System.out.println("send to browser success!!!");
                } else {
//                        System.out.println("send to browser failed!!!");
                }
            });
        } else {
//            System.out.println("have no channel to Browser!!!");
        }
    }
}
