package com.fnet.out.server.messageResolver;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;

class TransferResolver implements MessageResolver {

    @Override
    public void resolve(Message message, Sender sender) {
        sender.sendBytesToBrowser(message);
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
