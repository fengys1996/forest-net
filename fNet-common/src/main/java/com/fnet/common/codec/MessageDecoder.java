package com.fnet.common.codec;

import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.common.transfer.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * decoder(bytes -> message)
 */
public class MessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        byte messageType = in.readByte();
        int outerChannelId = in.readInt();
        int length = in.readInt();
        if (length == 0) {
            out.add(new Message(MessageType.valueOf(messageType), outerChannelId, null));
        } else {
            byte[] data = new byte[length];
            in.readBytes(data);
            out.add(new Message(MessageType.valueOf(messageType), outerChannelId, data));
        }
    }
}
