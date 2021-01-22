package com.fnet.out.server.handler;

import com.fnet.common.config.OuterServerConfig;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.out.server.domainCenter.DomainDataService;
import com.fnet.out.server.domainCenter.DomainInfo;
import com.fnet.common.authCenter.Authencator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
@Sharable
public class AuthHandler extends SimpleChannelInboundHandler<Message> {

    Sender sender;
    Authencator authencator;
    DomainDataService domainDataService;
    OuterServerConfig config;

    public AuthHandler(Sender sender, Authencator authencator, DomainDataService domainDataService, OuterServerConfig config) {
        this.sender = sender;
        this.authencator = authencator;
        this.domainDataService = domainDataService;
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        try {
            Channel channel = ctx.channel();
            SocketAddress socketAddress = ctx.channel().remoteAddress();

            if (socketAddress instanceof InetSocketAddress) {
                InetSocketAddress remoteAddress = (InetSocketAddress)socketAddress;
                DomainInfo domainInfo = null;
                boolean isRegisterSuccess =
                        authencator.registerAuth(msg, remoteAddress) && (domainInfo = issueAndSetupDomain(remoteAddress, channel)) != null;
                if (isRegisterSuccess) {
                    String domainAndPort = domainInfo.getDomainName() + ':' + config.getOspForBrowser();
                    sender.sendRegisterResponseMessage(true, domainAndPort.getBytes(), channel);
                } else {
                    sender.sendRegisterResponseMessage(false, null, channel);
                    channel.close();
                }
            }
        } finally {
            ReferenceCountUtil.release(msg.getPayLoad());
            log.debug("remove auth handler in pipeline!");
            ctx.pipeline().remove(this);
        }
    }

    private DomainInfo issueAndSetupDomain(InetSocketAddress remoteAddress, Channel transferChannel) {
        DomainInfo domainInfo = domainDataService.issueDomain();
        if (domainInfo == null)     return null;
        domainInfo.setAvailable(false);
        domainInfo.setBindClient(true);
        domainInfo.setBindClientIp(remoteAddress.getAddress().getHostAddress());
        domainInfo.setTransferChannel(transferChannel);
        return domainInfo;
    }
}
