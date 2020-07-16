package com.fnet.out.server.service;

import com.fnet.common.service.AbstractSender;
import com.fnet.common.service.TransferChannelService;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import io.netty.channel.Channel;

public class OuterSender extends AbstractSender {

    private static final OuterSender outerSender = new OuterSender();

    private OuterSender(){

    }

    public static OuterSender getInstance() {
        return outerSender;
    }

    @Override
    public void sendBytesToBrowser(Message message) {
        Channel outerChannel;
        outerChannel = OuterChannelDataService.getInstance().getOuterChannelById(message.getOuterChannelId());

        if (outerChannel != null && outerChannel.isOpen()) {
            outerChannel.writeAndFlush(message.getData());
        } else {
            System.out.println("have no channel to Browser!!!");
        }
    }

    public void sendRegisterResponseMessage(boolean isSuccess) {
        Message message;
        Channel readyTransferChannel;
        if (isSuccess) {
            message = new Message(MessageType.REGISTER_RESULT, 0, "true".getBytes());
        } else {
            message = new Message(MessageType.REGISTER_RESULT, 0, "false".getBytes());
        }
        readyTransferChannel = TransferChannelService.getInstance().getReadyTransferChannel();
        if (readyTransferChannel != null && readyTransferChannel.isOpen()) {
            readyTransferChannel.writeAndFlush(message);
        }
    }
}
