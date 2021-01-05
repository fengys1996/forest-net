package com.fnet.out.server.service;

import com.fnet.common.config.Config;
import com.fnet.common.service.AbstractSender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

        if (outerChannel != null && outerChannel.isOpen()) {
            outerChannel.writeAndFlush(message.getData());
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
        channel.writeAndFlush(message);
    }
}
