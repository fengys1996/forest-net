package com.fnet.inner.server.handler;

import com.fnet.common.transfer.Transfer;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.inner.server.service.InnerSender;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class RegisterHandler extends SimpleChannelInboundHandler<Message>{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InnerSender innerSender = InnerSender.getInstance();
        Transfer transfer = innerSender.getTransfer();
        transfer.addTransferChannel(ctx.channel());
        innerSender.sendRegisterMessage(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        try {
            if (msg.getType() == MessageType.REGISTER_RESULT) {
                byte[] data = msg.getData();
                if (data != null) {
                    String result = new String(data);
                    if ("true".equals(result)) {
                        log.info("Inner server register success!");
                        return;
                    } else {
                        InnerSender.getInstance().getTransfer().removeTransferChannel(ctx.channel());
                        log.info("Inner server register failed!");
                    }
                }
            }
        } finally {
            ctx.pipeline().remove(this);
        }
    }
}
