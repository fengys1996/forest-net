package com.fnet.out.server.sender;

import com.fnet.common.service.Sender;
import com.fnet.common.tool.ObjectTool;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.fnet.out.server.sender.TransferCache.*;

/**
 * @author fys
 */
@Slf4j
@Component
public class DefaultOuterSenderImpl implements Sender {

    @Override
    public void sendMessageToTransferChannel(Message message) {

        // check message
        int outerChannelId = message.getOuterChannelId();
        if (outerChannelId == 0) {
            return;
        }

        Channel transferChannel =
                outerChannel2TransferChannelReleatedCache.get(message.getOuterChannelId());

        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.writeAndFlush(message);
        } else {
            message.release();
            log.info("transfer channel is not writable, so dicard some message!!!");
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

        if (ObjectTool.checkChannel(outerChannel)) {
            outerChannel.writeAndFlush(message.getPayLoad());
        } else {
            message.release();
            log.info("channel2browser is not writable, so dicard some message!!!");
        }
    }

    Message registerMessage = new Message();
    ByteBuf falseByteBuf = Unpooled.wrappedBuffer("false".getBytes());

    @Override
    public void sendRegisterResponseMessage(boolean isSuccess, byte[] data, Channel channel) {
        ByteBuf payload = isSuccess ? Unpooled.wrappedBuffer(data) : falseByteBuf;
        registerMessage.setPayLoad(payload);
        registerMessage.setType(MessageType.REGISTER_RESULT);
        if (ObjectTool.checkChannel(channel)) {
            channel.writeAndFlush(registerMessage);
        } else {
            if (isSuccess) {
                registerMessage.release();
                log.info("transfer channel is not writable, so dicard some message!!!");
            }
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

        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.write(message);
        } else {
            message.release();
            log.info("transfer channel is not writable, so dicard some message!!!");
        }
    }

    @Override
    public void flush(int outerChannelId) {
        if (outerChannelId == 0) {
            return;
        }

        Channel transferChannel =
                outerChannel2TransferChannelReleatedCache.get(outerChannelId);

        if (ObjectTool.checkChannel(transferChannel)) {
            transferChannel.flush();
        } else {
            log.info("[flush] channel is not active!");
        }
    }
}
