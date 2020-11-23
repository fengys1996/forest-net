package com.fnet.common.transfer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOutboundInvoker;

import java.util.ArrayList;
import java.util.List;

public class MultiChannelTransfer extends AbatractTransfer {

    /**
     * ArrayList is not thread safe, but netty thread for receiving connections is a single thread.
     * So addTransferChannel{@link #addTransferChannel(Channel)} is thread safe.
     */
    private final List<Channel> transferChannelList = new ArrayList<>();


    /**
     * To be optimized
     */
    @Override
    public Channel getAvailableTransferChannel(int outChannelID) {
        Channel channel = transferChannelList.get(outChannelID % transferChannelList.size());
        if (channel == null) {
            throw new NullPointerException("multi transfer channel is not available!");
        }
        if (!channel.isOpen()) {
            throw new ChannelException("a multi transfer channel is not open![hashcode = " + channel.hashCode() + "]");
        }
        return channel;
    }

    @Override
    public void addTransferChannel(Channel channel) {
        transferChannelList.add(channel);
    }

    @Override
    public void removeTransferChannel(Channel channel) {
        transferChannelList.remove(channel);
    }

    @Override
    public void free() {
        transferChannelList.forEach(ChannelOutboundInvoker::close);
        transferChannelList.clear();
    }
}
