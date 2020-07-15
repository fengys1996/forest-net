package com.fnet.common.codec;

import com.fnet.common.transferProtocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * encoder(message -> bytes)
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getType().getCode());
        out.writeInt(msg.getOuterChannelId());
        if (msg.getData() == null) {
            out.writeInt(0);
        } else {
            out.writeInt(msg.getData().length);
            out.writeBytes(msg.getData());
        }
    }
}
