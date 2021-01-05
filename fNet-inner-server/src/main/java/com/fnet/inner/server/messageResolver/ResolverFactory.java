package com.fnet.inner.server.messageResolver;

import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;

import java.util.ArrayList;
import java.util.List;

class ResolverFactory {

    private static ResolverFactory resolverFactory = new ResolverFactory();
    private static final List<MessageResolver> resolvers = new ArrayList<>();
    static {
        resolvers.add(new DisconnectResolver());
        resolvers.add(new HeartBeatResolver());
        resolvers.add(new TransferResolver());
    }

    private ResolverFactory() {

    }

    public static ResolverFactory getInstance() {
        return resolverFactory;
    }

    public MessageResolver getMessageResolver(Message message) {
        for (MessageResolver resolver : resolvers) {
            if (resolver.isSupport(message)) {
                return resolver;
            }
        }
        throw new RuntimeException("can not find resolver, message type:" + message.getType());
    }
}
