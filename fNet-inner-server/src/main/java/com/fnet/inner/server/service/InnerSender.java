package com.fnet.inner.server.service;

import com.fnet.common.config.Config;
import com.fnet.common.service.AbstractSender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.Channel;

public class InnerSender extends AbstractSender {

    @Override
    public void sendBytesToRealServer(Message message) {
        Channel innerChannel = Outer2InnerInfoService.getInstance().getInnerChannel(message.getOuterChannelId());
        sendBytesToRealServer(innerChannel, message);
    }

    @Override
    public void sendBytesToRealServer(Channel channel, Message message) {
        if (channel == null) {
        } else if(channel.isOpen()) {
            channel.writeAndFlush(message.getData());
        }
    }

    @Override
    public void sendRegisterMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(new Message(MessageType.REGISTER, 0, Config.PASSWORD.getBytes()));
        }
    }

    @Override
    public void sendHeartBeatMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(HEART_BEAT_MESSAGE);
        }
    }
}
