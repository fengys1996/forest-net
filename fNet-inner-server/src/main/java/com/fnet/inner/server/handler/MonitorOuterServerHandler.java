package com.fnet.inner.server.handler;

import com.fnet.common.transfer.Transfer;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.inner.server.service.InnerSender;
import com.fnet.inner.server.messageResolver.ResolverContext;
import com.fnet.inner.server.tool.CloseHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorOuterServerHandler extends ChannelInboundHandlerAdapter {

    private static boolean firstInit = true;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InnerSender innerSender = InnerSender.getInstance();
        Transfer transfer = innerSender.getTransfer();
        transfer.addTransferChannel(ctx.channel());
        synchronized (MonitorOuterServerHandler.class) {
            if (firstInit) {
                innerSender.sendRegisterMessage(ctx.channel());
                firstInit = false;
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResolverContext.resolverMessage((Message) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("channel inactive! Transfer channel is removed!");
        InnerSender.getInstance().getTransfer().removeTransferChannel(ctx.channel());
        CloseHelper.closeInnerServer();
        /*boolean haveOenChannel = TransferChannelService.getInstance().isHaveOpenChannel();
        if (!haveOenChannel)  CloseHelper.closeInnerServer();*/
    }
}
