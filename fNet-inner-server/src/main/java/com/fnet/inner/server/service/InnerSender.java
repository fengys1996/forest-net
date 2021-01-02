package com.fnet.inner.server.service;

import com.fnet.common.config.Config;
import com.fnet.common.service.AbstractSender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.Channel;

public class InnerSender extends AbstractSender {

    private static final InnerSender innerSender = new InnerSender();

    private InnerSender(){

    }

    public static InnerSender getInstance() {
        if (Config.TRANSFER_CHANNEL_NUMBERS > 1) {
            innerSender.setMultiTransfer();
        }
        return innerSender;
    }

    public void sendBytesToRealServer(Message message) {
        Channel innerChannel = ContactOfOuterToInnerChannel.getInstance().getInnerChannel(message.getOuterChannelId());
        sendBytesToRealServer(innerChannel, message);
    }

    public void sendBytesToRealServer(Channel channel, Message message) {
        if (channel == null) {
        } else if(channel.isOpen()) {
            channel.writeAndFlush(message.getData());
        }
    }

    public void sendRegisterMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(new Message(MessageType.REGISTER, 0, Config.PASSWORD.getBytes()));
        }
    }

    public void sendHeartBeatMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(HEART_BEAT_MESSAGE);
        }
    }
}
