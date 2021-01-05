package com.fnet.inner.server.handler;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.inner.server.service.Outer2InnerInfoService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorRealServerHandler extends ChannelInboundHandlerAdapter {

    Message message;
    Sender sender;
    Outer2InnerInfoService outer2InnerInfoService;

    public MonitorRealServerHandler(Message message, Sender sender, Outer2InnerInfoService outer2InnerInfoService) {
        this.message = message;
        this.sender = sender;
        this.outer2InnerInfoService = outer2InnerInfoService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("A channel connect real server!");
        outer2InnerInfoService.addToMap(message.getOuterChannelId(), ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        sender.sendMessageToTransferChannel(new Message(MessageType.TRANSFER_DATA, message.getOuterChannelId(), (byte[])msg));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        sender.flush(message.getOuterChannelId());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("A channel disconnect real server!");
        outer2InnerInfoService.removeFromMap(message.getOuterChannelId());
        ctx.channel().close();
        super.channelInactive(ctx);
    }
}
