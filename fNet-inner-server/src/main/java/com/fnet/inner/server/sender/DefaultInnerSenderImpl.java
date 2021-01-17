package com.fnet.inner.server.sender;

import com.fnet.common.config.Config;
import com.fnet.common.service.Sender;
import com.fnet.common.tool.ObjectTool;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.fnet.common.transfer.protocol.MessageConstant.*;

@Slf4j
@Component
public class DefaultInnerSenderImpl implements Sender {

    @Override
    public void sendMessageToTransferChannel(Message message) {
        Channel transferChannel = TransferCache.getTransferChannel();
        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.writeAndFlush(message);
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
            channel.writeAndFlush(message.getData());
        }
    }

    Message registerMessage = new Message(MessageType.REGISTER, 0, null);

    @Override
    public void sendRegisterMessage(Channel channel) {
        if (ObjectTool.checkChannel(channel)) {
            registerMessage.setData(Config.PASSWORD.getBytes());
            channel.writeAndFlush(registerMessage);
        }
    }

    @Override
    public void sendHeartBeatMessage(Channel channel) {
        if (ObjectTool.checkChannel(channel)) {
            channel.writeAndFlush(HEART_BEAT_MESSAGE);
        }
    }
}
