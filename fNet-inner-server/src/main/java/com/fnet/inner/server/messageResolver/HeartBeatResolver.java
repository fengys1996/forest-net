package com.fnet.inner.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.inner.server.service.InnerSender;

public class HeartBeatResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
        InnerSender.getInstance().sendHearBeatResponseMessage();
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.HEART_BEAT;
    }
}
