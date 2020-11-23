package com.fnet.out.server.handler;

import com.fnet.common.transfer.protocol.Message;
import com.fnet.out.server.messageResolver.ResolverContext;
import com.fnet.out.server.service.OuterSender;
import com.fnet.out.server.tool.CloseHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorInnerServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        log.info("Transfer channel connect!");
        OuterSender.getInstance().getTransfer().addTransferChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResolverContext.resolverMessage((Message) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Transfer channel disconnect!");
        OuterSender.getInstance().getTransfer().removeTransferChannel(ctx.channel());
        CloseHelper.clearData();
        /*boolean haveOenChannel = TransferChannelService.getInstance().isHaveOpenChannel();
        if (!haveOenChannel)  CloseHelper.clearData();*/
    }
}
