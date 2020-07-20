package com.fnet.out.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.service.OuterSender;

public class HeartBeatResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
        OuterSender.getInstance().sendHearBeatResponseMessage();
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.HEART_BEAT;
    }
}
