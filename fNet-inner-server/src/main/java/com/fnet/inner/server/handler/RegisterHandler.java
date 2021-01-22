package com.fnet.inner.server.handler;

import com.fnet.common.authCenter.AuthToken;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import com.fnet.inner.server.sender.TransferCache;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class RegisterHandler extends SimpleChannelInboundHandler<Message>{

    Sender sender;
    String password;

    public RegisterHandler(Sender sender, String password) {
        this.sender = sender;
        this.password = password;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        sender.sendMessageToTransferChannel(generateRegisterMessage(), ctx.channel());
        super.channelActive(ctx);
    }

    Message registerMessage = new Message(MessageType.REGISTER, 0, null);
    private Message generateRegisterMessage() {
        long currentTimeMillis = System.currentTimeMillis();
        AuthToken authToken =
                AuthToken.createToken(currentTimeMillis, (currentTimeMillis + password).getBytes());
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeLong(currentTimeMillis);
        buffer.writeBytes(authToken.getToken());
        registerMessage.setPayLoad(buffer);
        return registerMessage;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        try {
            if (msg.getType() == MessageType.REGISTER_RESULT) {
                byte[] data = ByteBufUtil.getBytes(msg.getPayLoad());
                if (data != null) {
                    String result = new String(data);
                    if ("false".equals(result)) {
                        TransferCache.removeTransferChannel();
                        log.info("Inner server register failed!");
                    } else {
                        log.info("Inner server register success!Domain name = [" + result + ']');
                    }
                }
            }
        } finally {
            ReferenceCountUtil.release(msg.getPayLoad());
            ctx.pipeline().remove(this);
        }
    }
}
