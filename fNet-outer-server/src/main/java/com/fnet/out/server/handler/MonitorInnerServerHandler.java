package com.fnet.out.server.handler;

import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.out.server.domainCenter.DomainDataService;
import com.fnet.out.server.authCenter.AuthService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Sharable
public class MonitorInnerServerHandler extends ChannelInboundHandlerAdapter {

    public Sender sender;
    MessageResolver resolver;
    DomainDataService domainDataService;

    public MonitorInnerServerHandler(Sender sender, MessageResolver resolver, AuthService authService, DomainDataService domainDataService) {
        this.sender = sender;
        this.resolver = resolver;
        this.domainDataService = domainDataService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Transfer channel connect!");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        resolver.resolve((Message) msg, sender);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        domainDataService.recoveryDomainByTransferChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause instanceof IOException) {
            if ("远程主机强迫关闭了一个现有的连接。".equals(cause.getMessage())) {
                log.info("远程主机强迫关闭了一个现有的连接。");
                return;
            }
        }
        ctx.fireExceptionCaught(cause);
    }
}
