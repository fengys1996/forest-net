package com.fnet.out.server.handler;

import com.fnet.common.transferProtocol.Message;
import com.fnet.out.server.data.TransferChannelData;
import com.fnet.out.server.messageResolver.ResolverContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MonitorInnerServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active! Transfer channel is added! The hashCode is " + ctx.channel().hashCode());
        TransferChannelData.getInstance().addTransferChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        ResolverContext.resolverMessage((Message) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inactive! Transfer channel is added! The hashCode is " + ctx.channel().hashCode());
        TransferChannelData.getInstance().removeTransferChannel(ctx.channel());
    }
}
