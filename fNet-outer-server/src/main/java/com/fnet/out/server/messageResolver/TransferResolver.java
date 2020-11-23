package com.fnet.out.server.messageResolver;

import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.out.server.service.OuterSender;

public class TransferResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
        OuterSender.getInstance().sendBytesToBrowser(message);
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
