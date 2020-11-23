package com.fnet.common.service;

import com.fnet.common.config.Config;
import com.fnet.common.transfer.MultiChannelTransfer;
import com.fnet.common.transfer.SingleChannelTransfer;
import com.fnet.common.transfer.Transfer;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.Channel;

public abstract class AbstractSender implements Sender {

    private static final Transfer DEFAULT_TRANSFER = new SingleChannelTransfer();
    private static final Transfer MULTI_TRANSFER = new MultiChannelTransfer();

    public static final Message HEART_BEAT_MESSAGE = new Message(MessageType.HEART_BEAT);
    public static final Message HEART_BEAT_RESPONSE_MESSAGE = new Message(MessageType.HEART_BEAT_RESPONSE);
    public static final Message DISCONNECT_MESSAGE = new Message(MessageType.DISCONNECT);

    protected Transfer transfer = DEFAULT_TRANSFER;

    public Transfer getTransfer() {
        return transfer;
    }

    public void setMultiTransfer() {
        this.transfer = MULTI_TRANSFER;
    }

    @Override
    public void sendMessageToTransferChannel(Message message) {
        transfer.transferData(message);
    }

    @Override
    public void sendRegisterMessage() {
        transfer.transferData(new Message(MessageType.REGISTER, 0, Config.PASSWORD.getBytes()));
    }

    @Override
    public void sendRegisterMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(new Message(MessageType.REGISTER, 0, Config.PASSWORD.getBytes()));
        }
    }

    @Override
    public void sendHeartBeatMessage() {
        transfer.transferData(HEART_BEAT_MESSAGE);
    }

    @Override
    public void sendHeartBeatMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(HEART_BEAT_MESSAGE);
        }
    }

    @Override
    public void sendHearBeatResponseMessage() {
        transfer.transferData(HEART_BEAT_RESPONSE_MESSAGE);
    }

    @Override
    public void sendHearBeatResponseMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(HEART_BEAT_RESPONSE_MESSAGE);
        }
    }

    @Override
    public void sendDisconnectMessage() {
        transfer.transferData(DISCONNECT_MESSAGE);
    }

    @Override
    public void sendDisconnectMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(DISCONNECT_MESSAGE);
        }
    }
}
