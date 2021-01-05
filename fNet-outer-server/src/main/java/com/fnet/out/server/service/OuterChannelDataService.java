package com.fnet.out.server.service;

import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class OuterChannelDataService {

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

    public void clear() {
        Iterator<Map.Entry<Integer, Channel>> iterator = objOuterChannelMap.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Integer, Channel> next = iterator.next();
            Channel channel = next.getValue();
            channel.close();
            iterator.remove();
        }
    }
}
