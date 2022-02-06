package com.fnet.inner.server.sender;

import com.fnet.common.service.Sender;
import com.fnet.common.tool.ObjectTool;
import com.fnet.common.transfer.protocol.Message;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultInnerSenderImpl implements Sender {

    @Override
    public void sendMessageToTransferChannel(Message message) {
        Channel transferChannel = TransferCache.getTransferChannel();
        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.writeAndFlush(message);
        } else {
            message.release();
            log.info("transfer channel is not writable, so discard some message!!!");
        }
    }

    @Override
    public void sendMessageToTransferChannel(Message message, Channel channel) {
        if (ObjectTool.checkChannel(channel)) {
            channel.writeAndFlush(message);
        } else {
            message.release();
            log.info("transfer channel is not writable, so discard some message!!!");
        }
    }

    @Override
    public void sendMessageToTransferChannelNoFlush(Message message) {
        Channel transferChannel = TransferCache.getTransferChannel();
        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.write(message);
        } else {
            message.release();
            log.info("transfer channel is not writable, so discard some message!!!");
        }
    }

    @Override
    public void flush(int outChannelId) {
        Channel transferChannel = TransferCache.getTransferChannel();
        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.flush();
        } else {
            log.info("[flush] channel is not active!");
        }
    }

    @Override
    public void sendBytesToRealServer(Message message) {
        Channel innerChannel = TransferCache.getInnerChannel(message.getOuterChannelId());
        if (ObjectTool.checkChannel(innerChannel)) {
            sendBytesToRealServer(innerChannel, message);
        } else {
            message.release();
            log.info("channel2realserver is not writable, so discard some message!!!");
        }
    }

    @Override
    public void sendBytesToRealServer(Channel channel, Message message) {
        if (ObjectTool.checkChannel(channel)) {
            channel.writeAndFlush(message.getPayLoad());
        } else {
            message.release();
            log.info("channel2realserver is not writable, so discard some message!!!");
        }
    }
}
