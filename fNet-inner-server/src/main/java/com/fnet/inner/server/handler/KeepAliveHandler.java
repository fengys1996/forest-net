package com.fnet.inner.server.handler;

import com.fnet.common.service.AbstractSender;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeepAliveHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.info("write idle happen, so send neat heart to keep connection not closed by peer!");
            ctx.writeAndFlush(AbstractSender.HEART_BEAT_MESSAGE);
        }
        super.userEventTriggered(ctx, evt);
    }
}
