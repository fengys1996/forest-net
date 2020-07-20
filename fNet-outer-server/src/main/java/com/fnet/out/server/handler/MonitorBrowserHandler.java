package com.fnet.out.server.handler;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.service.OuterChannelDataService;
import com.fnet.out.server.service.OuterSender;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorBrowserHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("A channel connect browser!");
        OuterChannelDataService.getInstance().addToOuterChannelMap(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] bytes;
        int outerChannelId;

        bytes = (byte[])msg;
        outerChannelId = ctx.channel().hashCode();

        Message message = new Message(MessageType.TRANSFER_DATA, outerChannelId, bytes);
        OuterSender.getInstance().sendMessageToTransferChannel(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("A socket disconnect browser!");
        OuterChannelDataService.getInstance().removeOuterChannel(ctx.channel());
    }
}
