package com.fnet.out.server.messageResolver;

import com.fnet.common.transfer.Resolver;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;

public class ResolverContext implements Resolver {

    private static ResolverContext resolverContext = new ResolverContext();

    private ResolverContext() {

    }

    public static ResolverContext getInstance() {
        return resolverContext;
    }

    @Override
    public void resolverMessage(Message message) throws InterruptedException {
        MessageResolver messageResolver = ResolverFactory.getInstance().getMessageResolver(message);
        if (messageResolver != null) {
            messageResolver.resolve(message);
        }
    }
}
