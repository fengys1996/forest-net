package com.fnet.out.server.handler;

import com.fnet.common.config.Config;
import com.fnet.common.net.TcpServer;
import com.fnet.common.service.ThreadPoolUtil;
import com.fnet.common.transfer.Transfer;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.out.server.messageResolver.ResolverContext;
import com.fnet.out.server.service.AuthService;
import com.fnet.out.server.service.OuterChannelDataService;
import com.fnet.out.server.service.OuterSender;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Slf4j
public class MonitorInnerServerHandler extends ChannelInboundHandlerAdapter {

    private static volatile boolean isMonitorBrower = false;
    private static int numsOfInvalidChannel = 0;
    private static final Object lock = new Object();

    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        log.info("Transfer channel connect!");
        Transfer transfer = OuterSender.getInstance().getTransfer();
        transfer.addTransferChannel(ctx.channel());
        // if init success, then monitor browser
        if (transfer.getNumsOfTransferChannel() == Config.TRANSFER_CHANNEL_NUMBERS && !isMonitorBrower) {
            startMonitorBrowser();
            isMonitorBrower = true;
            log.info("last init channels!");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResolverContext.resolverMessage((Message) msg);
    }

    /**
     * 如果transferChannel失效的话, 需要关闭对应channel, 并且从transfer的channel list中删除对应的channel信息.
     * 如果全部tansferChannel失效的话, 则需要释放和浏览器的连接,并且清空注册信息, 并且从OuterChannelDataService的list信息中删除对应channel信息.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("Transfer channel disconnect!");
        OuterSender.getInstance().getTransfer().removeTransferChannel(channel);
        channel.close();

        synchronized (lock) {
            if (++numsOfInvalidChannel == Config.TRANSFER_CHANNEL_NUMBERS) {
                log.info("All transfer channel disconnect!");
                OuterChannelDataService.getInstance().clear();
                AuthService.getInstance().clearRegisterAuthInfo();
                numsOfInvalidChannel = 0;
            }
        }
    }

    private void startMonitorBrowser() {
        ExecutorService commonExecutor;
        commonExecutor = ThreadPoolUtil.getCommonExecutor();
        commonExecutor.execute(() -> {
            try {
                new TcpServer().startMonitor(Config.OUTER_REMOTE_PORT, new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayEncoder(),
                                              new ByteArrayDecoder(),
                                              new MonitorBrowserHandler());
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
