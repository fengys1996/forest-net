package com.fnet.inner.server.handler;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.inner.server.sender.TransferCache;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorRealServerHandler extends ChannelInboundHandlerAdapter {

    Message message;
    Sender sender;

    public MonitorRealServerHandler(Message message, Sender sender) {
        this.message = message;
        this.sender = sender;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("A channel connect real server!");
        TransferCache.addToMap(message.getOuterChannelId(), ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        sender.sendMessageToTransferChannelNoFlush(new Message(MessageType.TRANSFER_DATA, message.getOuterChannelId(), (ByteBuf) msg));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        sender.flush(message.getOuterChannelId());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("A channel disconnect real server!");
        TransferCache.removeFromMap(message.getOuterChannelId());
        ctx.channel().close();
        super.channelInactive(ctx);
    }
}
