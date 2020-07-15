package com.fnet.common.handler;

import com.fnet.common.service.AbstractSender;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.common.transferProtocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author fys
 */
public class CommonHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush(AbstractSender.HEART_BEAT_MESSAGE);
        }
    }
}
