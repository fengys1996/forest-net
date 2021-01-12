package com.fnet.out.server.sender;

import io.netty.channel.Channel;

/**
 * @author fys
 */
public class SenderTool {
    public static boolean checkChannel(Channel channel) {
        return channel != null && channel.isActive() && channel.isWritable();
    }
}
