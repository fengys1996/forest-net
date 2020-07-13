package com.fnet.inner.server.messageResolver;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;

public class ResolverContext {
    public static void resolverMessage(Message message) throws InterruptedException {
        MessageResolver messageResolver = ResolverFactory.getInstance().getMessageResolver(message);
        if (messageResolver != null) {
            messageResolver.resolve(message);
        }
    }
}
