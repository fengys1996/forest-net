package com.fnet.out.server.handler;

import com.fnet.common.service.AbstractSender;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.out.server.service.AuthService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
@Sharable
public class AuthHandler extends SimpleChannelInboundHandler<Message> {

    Sender sender;
    AuthService authService;

    public AuthHandler(Sender sender, AuthService authService) {
        this.sender = sender;
        this.authService = authService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        try {
            Channel channel;
            SocketAddress socketAddress;

            channel = ctx.channel();
            socketAddress = ctx.channel().remoteAddress();

            if (socketAddress instanceof InetSocketAddress) {
                InetSocketAddress remoteAddress = (InetSocketAddress)socketAddress;
                if (authService.registerAuth(msg, remoteAddress)) {
                    sender.sendRegisterResponseMessage(true, channel);
                } else {
                    sender.sendRegisterResponseMessage(false, channel);
                    ((AbstractSender)sender).getTransfer().removeTransferChannel(channel);
                    channel.close();
                }
            }
        } finally {
            log.debug("remove auth handler in pipeline!");
            ctx.pipeline().remove(this);
        }
    }
}
