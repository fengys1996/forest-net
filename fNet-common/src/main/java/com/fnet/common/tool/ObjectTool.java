package com.fnet.common.tool;

import io.netty.channel.Channel;

/**
 * @author fys
 */
public class ObjectTool {
    public static boolean checkChannel(Channel channel) {
        boolean isReady = channel != null && channel.isActive() && channel.isWritable();
        if (! isReady) {
            System.out.println("~~~~~~~~~~~~");
        }
        return channel != null && channel.isActive() && channel.isWritable();
    }
}
