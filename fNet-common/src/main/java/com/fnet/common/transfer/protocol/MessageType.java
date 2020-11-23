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
     * update the map in inner server, contain the Correspondence of outer channel and inner channel
     * direction: outer server -> inner server
     */
    UPDATE_CHANNEL_MAP_INFO(3),

    /**
     * transfer data
     * direction: outer server <-> inner server
     */
    TRANSFER_DATA(4),

    /**
     * disconnect
     * direction: outer server <-> inner server
     */
    DISCONNECT(5),

    /**
     * heart beat
     * direction: outer server <-> inner server
     */
    HEART_BEAT(6),

    /**
     * hear beat response
     * direction: outer server <-> inner server
     */
    HEART_BEAT_RESPONSE(7);

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
