package com.fnet.inner.server.messageQueue;

import com.fnet.common.transfer.protocol.Message;

public class MessageEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
