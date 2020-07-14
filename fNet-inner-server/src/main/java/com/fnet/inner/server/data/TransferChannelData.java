package com.fnet.inner.server.data;

import com.fnet.common.channel.ChannelInfo;
import com.fnet.common.channel.ChannelStatusEnum;
import io.netty.channel.Channel;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransferChannelData {

    private static TransferChannelData transferChannelData = new TransferChannelData();

    private TransferChannelData() {

    }

    public static TransferChannelData getInstance() {
        return transferChannelData;
    }

    private static int index = 0;

    private final List<ChannelInfo> transferChannelList = new ArrayList<>();

    public void addTransferChannel(Channel channel) {
        transferChannelList.add(new ChannelInfo(channel, ChannelStatusEnum.free));
    }

    public void removeTransferChannel(Channel channel) {
        transferChannelList.remove(channel);
    }

    public synchronized Channel getReadyTransferChannel() {
        return transferChannelList.get(0).getChannel();
        /*if (transferChannelList.size() > 0) {
            int pollingNum = 0;
            while (true) {
                int temp = index;
                index = (index + 1 >= transferChannelList.size()) ? 0 : index + 1;
                ChannelInfo channelInfo = transferChannelList.get(temp);
                if (channelInfo.getChannelStatus() == ChannelStatusEnum.free) {
                    return channelInfo.getChannel();
                }
                pollingNum++;
                if (pollingNum >= 10000) {
                    break;
                }
            }
            return null;
        } else {
            throw new RuntimeException("have no ready transfer channel!");
        }*/
    }

//    public synchronized void freeChannel(@NonNull Channel channel) {
    public void freeChannel(@NonNull Channel channel) {
        for (ChannelInfo channelInfo : transferChannelList) {
            if (channelInfo.getChannel() == channel) {
                channelInfo.setChannelStatus(ChannelStatusEnum.free);
            }
        }
    }

    public void clearAllChannel() {
        Iterator<ChannelInfo> iterator = transferChannelList.iterator();
        if (iterator.hasNext()) {
            ChannelInfo channelInfo = iterator.next();
            Channel channel = channelInfo.getChannel();
            channel.close();
            iterator.remove();
        }
    }

}
