package com.fnet.common.transfer.protocol;

import io.netty.buffer.ByteBuf;

/**
 * Carrier of communication(outer server <-> inner server)
 */
public class Message {

    private MessageType type;
    private int outerChannelId;
    private ByteBuf payLoad;

    public Message() {
    }

    public Message(MessageType type) {
        this.type = type;
        this.outerChannelId = 0;
        this.payLoad = null;
    }

    public Message(MessageType type, int outerChannelId, ByteBuf payLoad) {
        this.type = type;
        this.outerChannelId = outerChannelId;
        this.payLoad = payLoad;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getOuterChannelId() {
        return outerChannelId;
    }

    public void setOuterChannelId(int outerChannelId) {
        this.outerChannelId = outerChannelId;
    }

    public ByteBuf getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(ByteBuf payLoad) {
        this.payLoad = payLoad;
    }
}
