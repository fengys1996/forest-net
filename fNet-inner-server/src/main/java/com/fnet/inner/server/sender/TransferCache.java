package com.fnet.inner.server.sender;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransferCache {
    static Map<Integer, Channel> mapOfOuterToInnerChannel = new ConcurrentHashMap<>();

    public static void addToMap(Integer outerChannelId, Channel innerChannel) {
        mapOfOuterToInnerChannel.put(outerChannelId, innerChannel);
    }

    public static void removeFromMap(Integer outerChannelId) {
        mapOfOuterToInnerChannel.remove(outerChannelId);
    }

    public static Channel getInnerChannel(Integer outerChannelId) {
        return mapOfOuterToInnerChannel.get(outerChannelId);
    }

    private static Channel singleTransferChannel;

    public static Channel getTransferChannel() {
        return singleTransferChannel;
    }

    public static void addTransferChannel(Channel channel) {
        singleTransferChannel = channel;
    }

    public static void removeTransferChannel() {
        singleTransferChannel = null;
    }
}
