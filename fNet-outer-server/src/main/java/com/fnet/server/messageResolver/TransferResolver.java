package com.fnet.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;

public class TransferResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {

    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
