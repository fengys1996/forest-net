package com.fnet.common.transfer.protocol;

public interface MessageResolver {
    /**
     * resolve Message
     */
    void resolve(Message message) throws InterruptedException;

    boolean isSupport(Message message);
}
