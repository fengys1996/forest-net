package com.fnet.inner.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.inner.server.tool.CloseHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterResultResolver implements MessageResolver {
    @Override
    public void resolve(Message message) throws InterruptedException {
        byte[] data = message.getData();
        if (data != null) {
            String s = new String(data);
            if ("true".equals(s)) {
                log.info("Inner server register success!");
                return;
            } else {
                log.info("Register failed! Inner server will close!");
                CloseHelper.closeInnerServer();
            }
        }
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.REGISTER_RESULT;
    }
}
