package com.fnet.common.codec;

import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.common.transfer.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * decoder(bytes -> message)
 */
public class MessageDecoder extends MessageToMessageDecoder {

    @SuppressWarnings("unchecked")
    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        byte messageType = in.readByte();
        int outerChannelId = in.readInt();

        ByteBuf payload = null;
        int length = in.readInt();
        if (length != 0) {
            payload = in.retainedSlice(in.readerIndex(), length);
        }
        Message message = new Message(MessageType.valueOf(messageType), outerChannelId, payload);
        out.add(message);
    }
}
