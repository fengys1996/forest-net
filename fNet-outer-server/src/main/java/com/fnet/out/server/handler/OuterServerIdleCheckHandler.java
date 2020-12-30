package com.fnet.out.server.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Sharable
public class OuterServerIdleCheckHandler extends IdleStateHandler {

    public OuterServerIdleCheckHandler() {
        super(15, 0, 0 , TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            log.info("Idle check happen, so close the connection!");
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }
}
