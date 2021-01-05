package com.fnet.inner.server.service;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Outer2InnerInfoService {

    private static final Outer2InnerInfoService contactOfOuterToInnerChannel = new Outer2InnerInfoService();

    private Outer2InnerInfoService() {

    }

    public static Outer2InnerInfoService getInstance() {
        return contactOfOuterToInnerChannel;
    }

    private Map<Integer, Channel> mapOfOuterToInnerChannel = new ConcurrentHashMap<>();

    public void addToMap(Integer outerChannelId, Channel innerChannel) {
        mapOfOuterToInnerChannel.put(outerChannelId, innerChannel);
    }

    public void removeFromMap(Integer outerChannelId) {
        mapOfOuterToInnerChannel.remove(outerChannelId);
    }

    public Channel getInnerChannel(Integer outerChannelId) {
        return mapOfOuterToInnerChannel.get(outerChannelId);
    }

    public void clear() {
        Iterator<Map.Entry<Integer, Channel>> iterator = mapOfOuterToInnerChannel.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Integer, Channel> next = iterator.next();
            Channel channel = next.getValue();
            channel.close();
            iterator.remove();
        }
    }
}
