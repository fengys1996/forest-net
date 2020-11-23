package com.fnet.out.server.messageResolver;

import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;

public class ResolverContext {
    public static void resolverMessage(Message message) throws InterruptedException {
        MessageResolver messageResolver = ResolverFactory.getInstance().getMessageResolver(message);
        if (messageResolver != null) {
            messageResolver.resolve(message);
        }
    }
}
