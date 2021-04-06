package com.fnet.inner.server.messageResolver;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.inner.server.messageQueue.MessageEvent;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferResolver implements MessageResolver {

    RingBuffer<MessageEvent> ringBuffer;

    public TransferResolver(RingBuffer<MessageEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void resolve(Message message, Sender sender) throws InterruptedException {
        long seq = -2;
        try {
            seq = ringBuffer.tryNext();
            MessageEvent messageEvent = ringBuffer.get(seq);
            messageEvent.setMessage(message);
        } catch (InsufficientCapacityException e) {
            log.info("message queue is full,so discard some messages!");
        } finally {
            if (seq != -2) {
                ringBuffer.publish(seq);
            }
        }
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.TRANSFER_DATA;
    }
}
