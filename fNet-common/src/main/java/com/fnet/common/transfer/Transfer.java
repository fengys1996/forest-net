package com.fnet.common.transfer;

import com.fnet.common.transfer.protocol.Message;
import io.netty.channel.Channel;

public interface Transfer {

    int getNumsOfTransferChannel();

    Channel getAvailableTransferChannel(int outChannelID);

    void addTransferChannel(Channel channel);

    void removeTransferChannel(Channel channel);

    void transferData(Message message);

    void free();
}
