package com.fnet.common.transfer.protocol;

import com.fnet.common.service.Sender;

public interface MessageResolver {
    /**
     * resolve Message
     */
    void resolve(Message message, Sender sender) throws InterruptedException;

    default boolean isSupport(Message message) {
        return true;
    }
}
