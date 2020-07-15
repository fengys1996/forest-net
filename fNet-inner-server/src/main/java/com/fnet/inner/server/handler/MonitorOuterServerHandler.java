package com.fnet.inner.server.handler;

import com.fnet.common.handler.CommonHandler;
import com.fnet.common.service.TransferChannelService;
import com.fnet.common.transferProtocol.Message;
import com.fnet.inner.server.service.InnerSender;
import com.fnet.inner.server.messageResolver.ResolverContext;
import io.netty.channel.ChannelHandlerContext;

public class MonitorOuterServerHandler extends CommonHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TransferChannelService.getInstance().addTransferChannel(ctx.channel());
        InnerSender.getInstance().sendRegisterMessage(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Thread name = " + Thread.currentThread().getName());
        ResolverContext.resolverMessage((Message) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        TransferChannelService.getInstance().removeTransferChannel(ctx.channel());
    }
}
