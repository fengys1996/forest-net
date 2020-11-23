package com.fnet.inner.server.messageResolver;

import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.inner.server.tool.CloseHelper;

public class DisconnectResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
        CloseHelper.closeInnerServer();
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.DISCONNECT;
    }
}
