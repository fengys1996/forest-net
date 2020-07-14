package com.fnet.inner.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.inner.server.data.ContactOfOuterToInnerChannel;

public class DisconnectResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
        // close all channel of transfer
//        ContactOfOuterToInnerChannel.getInstance().clear();
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.DISCONNECT;
    }
}
