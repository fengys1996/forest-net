package com.fnet.inner.server.messageResolver;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;

class DisconnectResolver implements MessageResolver {

    @Override
    public void resolve(Message message, Sender sender) {
        // temporary do nothing
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.DISCONNECT;
    }
}
