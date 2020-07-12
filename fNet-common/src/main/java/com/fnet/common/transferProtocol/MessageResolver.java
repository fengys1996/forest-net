package com.fnet.common.transferProtocol;

public interface MessageResolver {
    /**
     * resolve Message
     */
    void resolve(Message message) throws InterruptedException;

    boolean isSupport(Message message);
}
