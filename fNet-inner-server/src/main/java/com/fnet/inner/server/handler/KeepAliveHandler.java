package com.fnet.inner.server.handler;

import com.fnet.inner.server.service.InnerSender;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeepAliveHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.debug("write idle happen, so send neat heart to keep connection not closed by peer!");
            InnerSender.getInstance().sendHeartBeatMessage(ctx.channel());
        }
        super.userEventTriggered(ctx, evt);
    }
}