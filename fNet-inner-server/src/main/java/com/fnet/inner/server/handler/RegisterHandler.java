package com.fnet.inner.server.handler;

import com.fnet.common.service.AbstractSender;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class RegisterHandler extends SimpleChannelInboundHandler<Message>{

    Sender sender;

    public RegisterHandler(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        sender.sendRegisterMessage(ctx.channel());
        super.channelActive(ctx);
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
                        ((AbstractSender)sender).getTransfer().removeTransferChannel(ctx.channel());
                        log.info("Inner server register failed!");
                    }
                }
            }
        } finally {
            ctx.pipeline().remove(this);
        }
    }
}
