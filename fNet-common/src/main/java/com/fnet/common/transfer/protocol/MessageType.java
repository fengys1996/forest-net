package com.fnet.common.transfer.protocol;

public enum MessageType {

    /**
     * authentication and start monitor browser
     * direction: inner server -> outer server
     */
    REGISTER(1),

    /**
     * the result of authentication and register
     * direction: outer server -> inner server
     */
    REGISTER_RESULT(2),


    /**
     * transfer data
     * direction: outer server <-> inner server
     */
    TRANSFER_DATA(3),

    /**
     * disconnect
     * direction: outer server <-> inner server
     */
    DISCONNECT(4),

    /**
     * heart beat
     * direction: outer server <-> inner server
     */
    HEART_BEAT(5);

    private int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MessageType valueOf(int code) {
        for (MessageType item : MessageType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw new RuntimeException("NatxMessageType code error: " + code);
    }
}
