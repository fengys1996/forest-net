package com.fnet.common.channel;

import io.netty.channel.Channel;

public class ChannelInfo {

    private Channel channel;
    private ChannelStatusEnum channelStatus;

    public ChannelInfo() {
    }

    public ChannelInfo(Channel channel, ChannelStatusEnum channelStatus) {
        this.channel = channel;
        this.channelStatus = channelStatus;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ChannelStatusEnum getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(ChannelStatusEnum channelStatus) {
        this.channelStatus = channelStatus;
    }
}