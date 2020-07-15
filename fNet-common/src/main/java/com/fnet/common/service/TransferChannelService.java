package com.fnet.common.service;

import com.fnet.common.channel.ChannelInfo;
import com.fnet.common.channel.ChannelStatusEnum;
import io.netty.channel.Channel;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransferChannelService {

    private static TransferChannelService transferChannelData = new TransferChannelService();

    private TransferChannelService() {

    }

    public static TransferChannelService getInstance() {
        return transferChannelData;
    }

    private static int index = 0;

    private final List<ChannelInfo> transferChannelList = new ArrayList<>();

    public void addTransferChannel(Channel channel) {
        transferChannelList.add(new ChannelInfo(channel, ChannelStatusEnum.free));
    }

    public void removeTransferChannel(Channel channel) {
        for (ChannelInfo channelInfo : transferChannelList) {
            if (channelInfo.getChannel() == channel) {
                transferChannelList.remove(channelInfo);
                return;
            }
        }
    }

    public synchronized Channel getReadyTransferChannel() {
        if (transferChannelList.size() > 0) {
            int temp = index;
            index = (index + 1 >= transferChannelList.size()) ? 0 : index + 1;
            return transferChannelList.get(temp).getChannel();
        } else {
            throw new RuntimeException("have no ready transfer channel!");
        }
    }

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
