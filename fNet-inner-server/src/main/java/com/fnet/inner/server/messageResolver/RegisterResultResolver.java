package com.fnet.inner.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.inner.server.tool.CloseHelper;

public class RegisterResultResolver implements MessageResolver {
    @Override
    public void resolve(Message message) throws InterruptedException {
        byte[] data = message.getData();
        if (data != null) {
            String s = new String(data);
            if ("true".equals(s)) {
                return;
            } else {
                CloseHelper.closeInnerServer();
            }
        }

    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.REGISTER_RESULT;
    }
}
