package com.fnet.common.transfer;

import com.fnet.common.transfer.protocol.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;

public class SingleChannelTransfer extends AbatractTransfer {

    private Channel singleTransferChannel;

    @Override
    public int getNumsOfTransferChannel() {
        return singleTransferChannel == null ? 0 : 1;
    }

    /**
     * @param outChannelID is not used
     */
    @Override
    public Channel getAvailableTransferChannel(int outChannelID) {
        if (singleTransferChannel == null) {
            throw new NullPointerException("single transfer channel is not available!");
        }
        if (!singleTransferChannel.isOpen()) {
            throw new ChannelException("single transfer channel is not open!");
        }
        return singleTransferChannel;
    }

    @Override
    public void addTransferChannel(Channel channel) {
        singleTransferChannel = channel;
    }

    @Override
    public void removeTransferChannel(Channel channel) {
        singleTransferChannel = null;
    }

    @Override
    public void transferDataNoFlush(Message message) {
        if (singleTransferChannel != null) {
            singleTransferChannel.write(message);
        }
    }

    @Override
    public void flush(int outChannelId) {
        if (singleTransferChannel != null) {
            singleTransferChannel.flush();
        }
    }

    @Override
    public void free() {
        if (singleTransferChannel != null) {
            singleTransferChannel.close();
            singleTransferChannel = null;
        }
    }
}
