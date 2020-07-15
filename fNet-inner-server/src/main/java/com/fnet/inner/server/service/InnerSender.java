package com.fnet.inner.server.service;

import com.fnet.common.service.AbstractSender;
import com.fnet.common.transferProtocol.Message;
import io.netty.channel.Channel;

public class InnerSender extends AbstractSender {

    private static final InnerSender innerSender = new InnerSender();

    private InnerSender(){

    }

    public static InnerSender getInstance() {
        return innerSender;
    }

    @Override
    public void sendBytesToRealServer(Message message) {
        Channel innerChannel = ContactOfOuterToInnerChannel.getInstance().getInnerChannel(message.getOuterChannelId());
        sendBytesToRealServer(innerChannel, message);
    }

    @Override
    public void sendBytesToRealServer(Channel channel, Message message) {
        if (channel == null) {
            System.out.println("Have not inner channel to send bytes to real server!");
        } else if(channel.isOpen()) {
            channel.writeAndFlush(message.getData());
        }
    }

}
