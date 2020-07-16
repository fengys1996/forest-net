package com.fnet.common.service;

import com.fnet.common.config.Config;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import io.netty.channel.Channel;

public abstract class AbstractSender implements Sender {

    public static final Message HEART_BEAT_MESSAGE = new Message(MessageType.HEART_BEAT);
    public static final Message HEART_BEAT_RESPONSE_MESSAGE = new Message(MessageType.HEART_BEAT_RESPONSE);
    public static final Message DISCONNECT_MESSAGE = new Message(MessageType.DISCONNECT);

    @Override
    public void sendMessageToTransferChannel(Message message) {
        Channel readyTransferChannel = TransferChannelService.getInstance().getReadyTransferChannel();
        readyTransferChannel.writeAndFlush(message);
    }

    @Override
    public void sendRegisterMessage() {
        Channel readyTransferChannel = TransferChannelService.getInstance().getReadyTransferChannel();
        sendRegisterMessage(readyTransferChannel);
    }

    @Override
    public void sendRegisterMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(new Message(MessageType.REGISTER, 0, Config.PASSWORD.getBytes()));
        }
    }

    @Override
    public void sendHeartBeatMessage() {
        Channel readyTransferChannel = TransferChannelService.getInstance().getReadyTransferChannel();
        sendHeartBeatMessage(readyTransferChannel);
    }

    @Override
    public void sendHeartBeatMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(HEART_BEAT_MESSAGE);
        }
    }

    @Override
    public void sendHearBeatResponseMessage() {
        Channel readyTransferChannel = TransferChannelService.getInstance().getReadyTransferChannel();
        sendHearBeatResponseMessage(readyTransferChannel);
    }

    @Override
    public void sendHearBeatResponseMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(HEART_BEAT_RESPONSE_MESSAGE);
        }
    }

    @Override
    public void sendDisconnectMessage() {
        Channel readyTransferChannel = TransferChannelService.getInstance().getReadyTransferChannel();
        sendDisconnectMessage(readyTransferChannel);
    }

    @Override
    public void sendDisconnectMessage(Channel channel) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(DISCONNECT_MESSAGE);
        }
    }
}
