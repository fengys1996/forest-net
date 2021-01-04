package com.fnet.inner.server.handler;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.inner.server.service.ContactOfOuterToInnerChannel;
import com.fnet.inner.server.service.InnerSender;
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
        log.info("A channel connect real server!");
        ContactOfOuterToInnerChannel.getInstance().addToMap(message.getOuterChannelId(), ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        InnerSender.getInstance().sendMessageToTransferChannel(new Message(MessageType.TRANSFER_DATA, message.getOuterChannelId(), (byte[])msg));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        InnerSender.getInstance().flush(ctx.channel().hashCode());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("A channel disconnect real server!");
        ContactOfOuterToInnerChannel.getInstance().removeFromMap(message.getOuterChannelId());
        ctx.channel().close();
        super.channelInactive(ctx);
    }
}
