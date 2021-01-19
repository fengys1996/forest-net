package com.fnet.common.codec;

import com.fnet.common.transfer.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

/**
 * encoder(message -> bytes)
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = null;
        try {
            if (acceptOutboundMessage(msg)) {
                @SuppressWarnings("unchecked")
                Message cast = (Message) msg;
                buf = ctx.alloc().compositeBuffer();
                try {
                    encode(ctx, cast, buf);
                } finally {
                    ReferenceCountUtil.release(cast);
                }

                if (buf.isReadable()) {
                    ctx.write(buf, promise);
                } else {
                    buf.release();
                    ctx.write(Unpooled.EMPTY_BUFFER, promise);
                }
                buf = null;
            } else {
                ctx.write(msg, promise);
            }
        } catch (EncoderException e) {
            throw e;
        } catch (Throwable e) {
            throw new EncoderException(e);
        } finally {
            if (buf != null) {
                buf.release();
            }
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf cBuf) {

        CompositeByteBuf compositeByteBuf = (CompositeByteBuf)cBuf;
        ByteBuf inBuf = ctx.alloc().buffer();
        inBuf.writeByte(msg.getType().getCode());
        inBuf.writeInt(msg.getOuterChannelId());

        ByteBuf payLoad = msg.getPayLoad();
        if (payLoad == null) {
            inBuf.writeInt(0);
        } else {
            inBuf.writeInt(payLoad.readableBytes());
        }
        compositeByteBuf.addComponent(true, inBuf);
        if (payLoad != null) {
            compositeByteBuf.addComponent(true, payLoad);
        }
    }
}
