package com.fnet.out.server.handler;

import com.fnet.common.transfer.protocol.Message;
import com.fnet.out.server.service.AuthService;
import com.fnet.out.server.service.OuterSender;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        try {
            AuthService authService;
            OuterSender outerSender;
            Channel channel;
            SocketAddress socketAddress;

            authService = AuthService.getInstance();
            outerSender = OuterSender.getInstance();
            channel = ctx.channel();
            socketAddress = ctx.channel().remoteAddress();

            if (socketAddress instanceof InetSocketAddress) {
                InetSocketAddress remoteAddress = (InetSocketAddress)socketAddress;
                if (authService.registerAuth(msg, remoteAddress)) {
                    outerSender.sendRegisterResponseMessage(true, channel);
                } else {
                    outerSender.sendRegisterResponseMessage(false, channel);
                    outerSender.getTransfer().removeTransferChannel(channel);
                    channel.close();
                }
            }
        } finally {
            log.debug("remove auth handler in pipeline!");
            ctx.pipeline().remove(this);
        }
    }
}
