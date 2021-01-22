package com.fnet.common.authCenter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class AuthMessage {

    private byte[] token;
    private long timestamp;

    public byte[] getToken() {
        return token;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static AuthMessage buildMessage(ByteBuf payLoad) {
        AuthMessage authMessage = null;
        if (payLoad != null && payLoad.readableBytes() > 8) {
            authMessage = new AuthMessage();
            authMessage.timestamp = payLoad.readLong();
            authMessage.token = ByteBufUtil.getBytes(payLoad);
        }
        return authMessage;
    }

}
