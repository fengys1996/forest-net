package com.fnet.out.server.sender;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransferCache {
    /**
     * outer channel hash code ---- transfer channel
     */
    static Map<Integer, Channel> outerChannel2TransferChannelReleatedCache = new ConcurrentHashMap<>(100);

    /**
     * outer channel hash code ---- outer channel
     */
    static Map<Integer, Channel> outerChannelMap = new ConcurrentHashMap<>(100);

    public static void addOuterChannel(Channel outerChannel, Channel transferChannel) {
        int hashcode = outerChannel.hashCode();
        outerChannelMap.put(hashcode, outerChannel);
        outerChannel2TransferChannelReleatedCache.put(hashcode, transferChannel);
    }

    public static void removeOuterChannel(Channel outChannel) {
        int hashCode = outChannel.hashCode();
        outerChannelMap.remove(hashCode);
        outerChannel2TransferChannelReleatedCache.remove(hashCode);
    }
}
