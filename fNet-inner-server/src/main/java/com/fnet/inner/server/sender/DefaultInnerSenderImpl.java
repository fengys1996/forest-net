package com.fnet.inner.server.sender;

import com.fnet.common.config.InnerServerConfig;
import com.fnet.common.service.Sender;
import com.fnet.common.tool.ObjectTool;
import com.fnet.common.transfer.protocol.Message;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultInnerSenderImpl implements Sender {

    @Autowired
    InnerServerConfig config;

    @Override
    public void sendMessageToTransferChannel(Message message) {
        Channel transferChannel = TransferCache.getTransferChannel();
        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.writeAndFlush(message);
        }
    }

    @Override
    public void sendMessageToTransferChannel(Message message, Channel channel) {
        if (ObjectTool.checkChannel(channel)) {
            channel.writeAndFlush(message);
        }
    }

    @Override
    public void sendMessageToTransferChannelNoFlush(Message message) {
        Channel transferChannel = TransferCache.getTransferChannel();
        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.write(message);
        }
    }

    @Override
    public void flush(int outChannelId) {
        Channel transferChannel = TransferCache.getTransferChannel();
        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.flush();
        }
    }

    @Override
    public void sendBytesToRealServer(Message message) {
        Channel innerChannel = TransferCache.getInnerChannel(message.getOuterChannelId());
        if (ObjectTool.checkChannel(innerChannel)) {
            sendBytesToRealServer(innerChannel, message);
        }
    }

    @Override
    public void sendBytesToRealServer(Channel channel, Message message) {
        if (ObjectTool.checkChannel(channel)) {
            channel.writeAndFlush(message.getPayLoad());
        }
    }
}
