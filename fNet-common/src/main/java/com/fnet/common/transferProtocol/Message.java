package com.fnet.common.transferProtocol;

/**
 * Carrier of communication(outer server <-> inner server)
 */
public class Message {

    private MessageType type;
    private int outerChannelId;
    private byte[] data;

    public Message() {
    }

    public Message(MessageType type, int outerChannelId , byte[] data) {
        this.type = type;
        this.outerChannelId = outerChannelId;
        this.data = data;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
