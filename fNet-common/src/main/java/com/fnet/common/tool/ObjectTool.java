package com.fnet.common.tool;

import io.netty.channel.Channel;

/**
 * @author fys
 */
public class ObjectTool {
    public static boolean checkChannel(Channel channel) {
        return channel != null && channel.isActive() && channel.isWritable();
    }
}
