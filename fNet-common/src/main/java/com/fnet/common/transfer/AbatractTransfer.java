package com.fnet.common.transfer;

import com.fnet.common.transfer.protocol.Message;
import io.netty.channel.Channel;

public abstract class AbatractTransfer implements Transfer {

    @Override
    public void transferData(Message message) {
        Channel availableTransferChannel = getAvailableTransferChannel(message.getOuterChannelId());
        if (availableTransferChannel != null) {
            availableTransferChannel.writeAndFlush(message);
        }
    }
}
