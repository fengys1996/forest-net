package com.fnet.out.server.sender;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import static com.fnet.out.server.sender.TransferCache.*;

/**
 * @author fys
 */
@Component
public class DefaultSenderImpl implements Sender {

    @Override
    public void sendMessageToTransferChannel(Message message) {

        // check message
        int outerChannelId = message.getOuterChannelId();
        if (outerChannelId == 0) {
            return;
        }

        Channel tranferChannel =
                outerChannel2TransferChannelReleatedCache.get(message.getOuterChannelId());

        if (SenderTool.checkChannel(tranferChannel)) {
            tranferChannel.writeAndFlush(message);
        }
    }

    @Override
    public void sendBytesToBrowser(Message message) {
        // check message
        int outerChannelId = message.getOuterChannelId();
        if (outerChannelId == 0) {
            return;
        }

        Channel outerChannel = outerChannelMap.get(outerChannelId);

        if (SenderTool.checkChannel(outerChannel)) {
            outerChannel.writeAndFlush(message.getData());
        }
    }

    Message registerMessage = new Message();
    byte[] trueBytes = "true".getBytes();
    byte[] falseBytes = "false".getBytes();

    @Override
    public void sendRegisterResponseMessage(boolean isSuccess, byte[] data, Channel channel) {
        registerMessage.setData(isSuccess ? data : falseBytes);
        registerMessage.setType(MessageType.REGISTER_RESULT);
        if (SenderTool.checkChannel(channel)) {
            channel.writeAndFlush(registerMessage);
        }
    }

    @Override
    public void sendMessageToTransferChannelNoFlush(Message message) {
        // check message
        int outerChannelId = message.getOuterChannelId();
        if (outerChannelId == 0) {
            return;
        }

        Channel transferChannel =
                outerChannel2TransferChannelReleatedCache.get(outerChannelId);

        if (SenderTool.checkChannel(transferChannel)) {
            transferChannel.write(message);
        }
    }

    @Override
    public void flush(int outerChannelId) {
        if (outerChannelId == 0) {
            return;
        }

        Channel tranferChannel =
                outerChannel2TransferChannelReleatedCache.get(outerChannelId);

        if (SenderTool.checkChannel(tranferChannel)) {
            tranferChannel.flush();
        }
    }
}
