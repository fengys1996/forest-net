package com.fnet.out.server.handler;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.out.server.service.OuterChannelDataService;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorBrowserHandler extends ChannelInboundHandlerAdapter {

    Sender sender;
    OuterChannelDataService outerChannelDataService;

    public MonitorBrowserHandler(Sender sender, OuterChannelDataService outerChannelDataService) {
        this.sender = sender;
        this.outerChannelDataService = outerChannelDataService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("A channel connect browser!");
        outerChannelDataService.addToOuterChannelMap(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] bytes;
        int outerChannelId;

        bytes = (byte[])msg;
        outerChannelId = ctx.channel().hashCode();

        Message message = new Message(MessageType.TRANSFER_DATA, outerChannelId, bytes);
        sender.sendMessageToTransferChannel(message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        sender.flush(ctx.channel().hashCode());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("A channel disconnect browser!");
        outerChannelDataService.removeOuterChannel(ctx.channel());
    }
}
