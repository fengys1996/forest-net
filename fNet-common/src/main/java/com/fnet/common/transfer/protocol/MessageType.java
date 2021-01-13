package com.fnet.common.transfer.protocol;

public enum MessageType {

    /**
     * authentication and start monitor browser
     * direction: inner server -> outer server
     */
    REGISTER((byte)1),

    /**
     * the result of authentication and register
     * direction: outer server -> inner server
     */
    REGISTER_RESULT((byte)2),

    /**
     * transfer data
     * direction: outer server <-> inner server
     */
    TRANSFER_DATA((byte)3),

    /**
     * disconnect
     * direction: outer server <-> inner server
     */
    DISCONNECT((byte)4),

    /**
     * heart beat
     * direction: outer server <-> inner server
     */
    HEART_BEAT((byte)5);

    private final byte code;

    MessageType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static MessageType valueOf(byte code) {
        for (MessageType item : values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw new RuntimeException("MessageType code error: " + code);
    }
}
