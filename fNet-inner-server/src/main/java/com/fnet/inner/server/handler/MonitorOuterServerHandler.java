package com.fnet.inner.server.handler;

import com.fnet.common.transferProtocol.Message;
import com.fnet.inner.server.data.Sender;
import com.fnet.inner.server.data.TransferChannelData;
import com.fnet.inner.server.messageResolver.ResolverContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MonitorOuterServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TransferChannelData.getInstance().addTransferChannel(ctx.channel());
        Sender.sendRegisterMessage(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Thread name = " + Thread.currentThread().getName());
        ResolverContext.resolverMessage((Message) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        TransferChannelData.getInstance().removeTransferChannel(ctx.channel());
    }
}
