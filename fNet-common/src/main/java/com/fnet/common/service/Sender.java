package com.fnet.common.service;


import com.fnet.common.transfer.protocol.Message;
import io.netty.channel.Channel;

public interface Sender {

    // common
    default void sendMessageToTransferChannel(Message message) {
        throw new UnsupportedOperationException();
    }

    default void sendMessageToTransferChannel(Message message, Channel channel) {
        throw new UnsupportedOperationException();
    }

    default void sendMessageToTransferChannelNoFlush(Message message) {
        throw new UnsupportedOperationException();
    }

    default void flush(int outChannelId) {
        throw new UnsupportedOperationException();
    }

    // inner server
    default void sendBytesToRealServer(Message message) {
        throw new UnsupportedOperationException();
    }

    default void sendBytesToRealServer(Channel channel, Message message) {
        throw new UnsupportedOperationException();
    }

    // outer server
    default void sendBytesToBrowser(Message message) {
        throw new UnsupportedOperationException();
    }

    default void sendRegisterResponseMessage(boolean isSuccess, byte[] data, Channel channel) {
        throw new UnsupportedOperationException();
    }
}

