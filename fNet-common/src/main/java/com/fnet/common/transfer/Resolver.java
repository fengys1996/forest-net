package com.fnet.common.transfer;

import com.fnet.common.transfer.protocol.Message;

public interface Resolver {

    void resolverMessage(Message message) throws InterruptedException;
}
