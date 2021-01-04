package com.fnet.inner.server.messageResolver;

import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;

import java.util.concurrent.LinkedBlockingQueue;

public class TransferResolver implements MessageResolver {

    public static LinkedBlockingQueue<Message> MESSAGE_QUEUE = new LinkedBlockingQueue<>(10000);

    @Override
    public void resolve(Message message) throws InterruptedException {
        MESSAGE_QUEUE.put(message);
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
