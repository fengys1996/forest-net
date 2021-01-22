package com.fnet.inner.server.handler;

import com.fnet.common.service.Sender;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import static com.fnet.common.transfer.protocol.MessageConstant.*;

@Slf4j
@Sharable
public class KeepAliveHandler extends ChannelDuplexHandler {

    Sender sender;

    public KeepAliveHandler(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.debug("write idle happen, so send beat heart to keep connection not closed by peer!");
            sender.sendMessageToTransferChannel(HEART_BEAT_MESSAGE, ctx.channel());
        }
        super.userEventTriggered(ctx, evt);
    }
}
