package com.fnet.out.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.data.OuterChannelData;
import com.fnet.out.server.data.TransferChannelData;

public class DisconnectResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
        // close all channel of transfer
        TransferChannelData.getInstance().clearAllChannel();
        OuterChannelData.getInstance().clear();
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.DISCONNECT;
    }
}
