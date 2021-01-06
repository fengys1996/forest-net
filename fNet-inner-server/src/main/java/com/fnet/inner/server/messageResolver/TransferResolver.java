package com.fnet.inner.server.messageResolver;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class TransferResolver implements MessageResolver {

    public static LinkedBlockingQueue<Message> MESSAGE_QUEUE = new LinkedBlockingQueue<>(10000);

    @Override
    public void resolve(Message message, Sender sender) throws InterruptedException {
        if (!MESSAGE_QUEUE.offer(message)) {
            log.info("message queue is full,so discard some messages!");
        }
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
