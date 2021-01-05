package com.fnet.inner.server.messageResolver;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import org.springframework.stereotype.Component;

@Component
public class ResolverContext implements MessageResolver {

    @Override
    public void resolve(Message message, Sender sender) throws InterruptedException {
        MessageResolver messageResolver = ResolverFactory.getInstance().getMessageResolver(message);
        if (messageResolver != null) {
            messageResolver.resolve(message, sender);
        }
    }
}
