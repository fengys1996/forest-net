package com.fnet.out.server.messageResolver;

import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.out.server.service.OuterSender;

public class HeartBeatResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.HEART_BEAT;
    }
}
