package com.fnet.out.server.service;

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
public class OuterSender extends AbstractSender {

    @Autowired
    OuterChannelDataService outerChannelDataService;

    public OuterSender() {
        if (Config.TRANSFER_CHANNEL_NUMBERS > 1)    transfer = MULTI_TRANSFER;
    }

    @Override
    public void sendBytesToBrowser(Message message) {
        Channel outerChannel;
        outerChannel = outerChannelDataService.getOuterChannelById(message.getOuterChannelId());

        if (outerChannel != null && outerChannel.isActive() && outerChannel.isWritable()) {
            outerChannel.writeAndFlush(message.getData());
        } else {
            log.info("channel is not writable, and discard some message!");
        }
    }

    @Override
    public void sendRegisterResponseMessage(boolean isSuccess, Channel channel) {
        Message message;
        if (isSuccess) {
            message = new Message(MessageType.REGISTER_RESULT, 0, "true".getBytes());
        } else {
            message = new Message(MessageType.REGISTER_RESULT, 0, "false".getBytes());
        }
        if (channel != null && channel.isActive() && channel.isWritable()) {
            channel.writeAndFlush(message);
        } else {
            log.info("channel is not writable, and discard some message!");
        }

    }
}
