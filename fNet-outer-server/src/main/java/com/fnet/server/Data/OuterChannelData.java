package com.fnet.server.Data;

import io.netty.channel.Channel;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class OuterChannelData {

    private static OuterChannelData outerChannelData = new OuterChannelData();

    private OuterChannelData() {

    }

    public static OuterChannelData getInstance() {
        return outerChannelData;
    }

    private Map<Integer, Channel> objOuterChannelMap = new HashMap<Integer, Channel>();

    public Channel getOuterChannelById(@NonNull Integer outerChannelId) {
        return objOuterChannelMap.get(outerChannelId);
    }

    public void addToOuterChannelMap(@NonNull Channel outerChannel) {
        objOuterChannelMap.put(outerChannel.hashCode(), outerChannel);
    }

    public void removeOuterChannel(@NonNull Channel channel) {
        objOuterChannelMap.remove(channel);
    }
}
