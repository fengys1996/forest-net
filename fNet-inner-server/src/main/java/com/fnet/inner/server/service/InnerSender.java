package com.fnet.inner.server.service;

import com.fnet.common.config.Config;
import com.fnet.common.service.AbstractSender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InnerSender extends AbstractSender {

    @Autowired
    Outer2InnerInfoService outer2InnerInfoService;

    public InnerSender() {
        if (Config.TRANSFER_CHANNEL_NUMBERS > 1)    transfer = MULTI_TRANSFER;
    }

    @Override
    public void sendBytesToRealServer(Message message) {
        Channel innerChannel = outer2InnerInfoService.getInnerChannel(message.getOuterChannelId());
        sendBytesToRealServer(innerChannel, message);
    }

    @Override
    public void sendBytesToRealServer(Channel channel, Message message) {
        if (channel != null && channel.isActive() && channel.isWritable()) {
            channel.writeAndFlush(message.getData());
        } else {
            log.info("channel is not writable, and discard some message!");
        }
    }

    @Override
    public void sendRegisterMessage(Channel channel) {
        if (channel != null && channel.isActive() && channel.isWritable()) {
            channel.writeAndFlush(new Message(MessageType.REGISTER, 0, Config.PASSWORD.getBytes()));
        } else {
            log.info("channel is not writable, and discard some message!");
        }
    }

    @Override
    public void sendHeartBeatMessage(Channel channel) {
        if (channel != null && channel.isActive() && channel.isWritable()) {
            channel.writeAndFlush(HEART_BEAT_MESSAGE);
        } else {
            log.info("channel is not writable, and discard some message!");
        }
    }
}
