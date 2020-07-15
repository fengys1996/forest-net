package com.fnet.common.service;

import com.fnet.common.transferProtocol.Message;
import io.netty.channel.Channel;

public interface Sender {
    /**
     * outer server need implements
     */
    default void sendBytesToBrowser(Message message) {
        throw new UnsupportedOperationException();
    }

    /**
     * inner server need implements
     */
    default void sendBytesToRealServer(Message message) {
        throw new UnsupportedOperationException();
    }

    default void sendBytesToRealServer(Channel channel, Message message) {
        throw new UnsupportedOperationException();
    }

    /**
     * common
     */
    void sendMessageToTransferChannel(Message message);

    void sendRegisterMessage();

    void sendRegisterMessage(Channel channel);

    void sendHeartBeatMessage();

    void sendHeartBeatMessage(Channel channel);

    void sendHearBeatResponseMessage();

    void sendHearBeatResponseMessage(Channel channel);

    void sendDisconnectMessage();

    void sendDisconnectMessage(Channel channel);
}
