package com.fnet.out.server.service;

import com.fnet.common.config.Config;
import com.fnet.common.service.AbstractSender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.Channel;

public class OuterSender extends AbstractSender {

    private static final OuterSender outerSender = new OuterSender();

    private OuterSender(){

    }

    public static OuterSender getInstance() {
        if (Config.TRANSFER_CHANNEL_NUMBERS > 1) {
            outerSender.setMultiTransfer();
        }
        return outerSender;
    }

    @Override
    public void sendBytesToBrowser(Message message) {
        Channel outerChannel;
        outerChannel = OuterChannelDataService.getInstance().getOuterChannelById(message.getOuterChannelId());

        if (outerChannel != null && outerChannel.isOpen()) {
            outerChannel.writeAndFlush(message.getData());
        }
    }

    public void sendRegisterResponseMessage(boolean isSuccess) {
        Message message;
        if (isSuccess) {
            message = new Message(MessageType.REGISTER_RESULT, 0, "true".getBytes());
        } else {
            message = new Message(MessageType.REGISTER_RESULT, 0, "false".getBytes());
        }
        transfer.transferData(message);
    }
}
