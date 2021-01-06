package com.fnet.common.transfer;

import com.fnet.common.transfer.protocol.Message;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbatractTransfer implements Transfer {

    @Override
    public void transferData(Message message) {
        Channel availableTransferChannel = getAvailableTransferChannel(message.getOuterChannelId());
        if (availableTransferChannel != null && availableTransferChannel.isActive() && availableTransferChannel.isWritable()) {
            availableTransferChannel.writeAndFlush(message);
        } else {
            log.info("channel is not writable, and discard some message!");
        }
    }

    @Override
    public void transferDataNoFlush(Message message) {
        Channel availableTransferChannel = getAvailableTransferChannel(message.getOuterChannelId());
        if (availableTransferChannel != null && availableTransferChannel.isActive() && availableTransferChannel.isWritable()) {
            availableTransferChannel.write(message);
        }
    }

    @Override
    public void flush(int outChannelId) {
        Channel availableTransferChannel = getAvailableTransferChannel(outChannelId);
        if (availableTransferChannel != null && availableTransferChannel.isActive()) {
            availableTransferChannel.flush();
        }
    }
}
