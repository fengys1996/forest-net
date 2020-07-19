package com.fnet.inner.server.handler;

import com.fnet.common.handler.CommonHandler;
import com.fnet.common.service.TransferChannelService;
import com.fnet.common.transferProtocol.Message;
import com.fnet.inner.server.service.InnerSender;
import com.fnet.inner.server.messageResolver.ResolverContext;
import com.fnet.inner.server.tool.CloseHelper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorOuterServerHandler extends CommonHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TransferChannelService.getInstance().addTransferChannel(ctx.channel());
        InnerSender.getInstance().sendRegisterMessage(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResolverContext.resolverMessage((Message) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive! Transfer channel is removed!");
        TransferChannelService.getInstance().removeTransferChannel(ctx.channel());
        boolean haveOenChannel = TransferChannelService.getInstance().isHaveOpenChannel();
        if (!haveOenChannel)  CloseHelper.closeInnerServer();
    }
}
