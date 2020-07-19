package com.fnet.inner.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;

public class HeartBeatResponseResolver implements MessageResolver {
    @Override
    public void resolve(Message message) throws InterruptedException {
        // temporary do nothing
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.HEART_BEAT_RESPONSE;
    }
}
