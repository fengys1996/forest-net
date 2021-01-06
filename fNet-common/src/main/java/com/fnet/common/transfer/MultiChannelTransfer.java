package com.fnet.common.transfer;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Because Low utilization rate, so deprecated
 */
@Deprecated
public class MultiChannelTransfer extends AbatractTransfer {

    public static final int MAX_NUM_OF_TRANSFER_CHANNEL = 10;

    private final List<Channel> transferChannelList = new ArrayList<>(MAX_NUM_OF_TRANSFER_CHANNEL);

    private final AtomicInteger numsOfTransferChannels = new AtomicInteger(0);

    @Override
    public synchronized int getNumsOfTransferChannel() {
        return numsOfTransferChannels.get();
    }

    @Override
    public Channel getAvailableTransferChannel(int outChannelID) {
        int index = Math.abs(outChannelID % transferChannelList.size());
        int circleNum = 0;
        while (circleNum < transferChannelList.size()) {
            if (transferChannelList.get(index) != null && transferChannelList.get(index).isOpen()) {
                return transferChannelList.get(index);
            }
            index = (index + 1) % transferChannelList.size();
            circleNum++;
        }
        return null;
    }

    @Override
    public synchronized void addTransferChannel(Channel channel) {
        for (int i = 0; i < transferChannelList.size(); i++) {
            if (transferChannelList.get(i) == null) {
                numsOfTransferChannels.addAndGet(1);
                transferChannelList.set(i, channel);
                return;
            }
        }
        numsOfTransferChannels.addAndGet(1);
        transferChannelList.add(channel);
    }

    @Override
    public void removeTransferChannel(Channel channel) {
        for (int i = 0; i < transferChannelList.size(); i++) {
            if (transferChannelList.get(i) == channel) {
                numsOfTransferChannels.addAndGet(-1);
                transferChannelList.set(i, null);
            }
        }
    }

    @Override
    public void free() {
        transferChannelList.forEach(o -> {
            if (o != null)  o.close();
        });
        transferChannelList.clear();
    }
}
