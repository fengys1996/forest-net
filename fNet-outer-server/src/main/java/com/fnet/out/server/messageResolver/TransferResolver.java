package com.fnet.out.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.data.Sender;

public class TransferResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
        Sender.sendBytesToBrowser(message);
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
