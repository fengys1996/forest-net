package com.fnet.out.server.handler;

import com.fnet.common.config.Config;
import com.fnet.common.handler.AbstractMonitorHandler;
import com.fnet.common.net.TcpServer;
import com.fnet.common.service.Sender;
import com.fnet.common.service.ThreadPoolUtil;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.out.server.service.AuthService;
import com.fnet.out.server.service.OuterChannelDataService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import static com.fnet.common.net.TcpServer.*;

@Slf4j
@Sharable
public class MonitorInnerServerHandler extends AbstractMonitorHandler {

    private volatile boolean isMonitorBrower = false;

    AuthService authService;
    OuterChannelDataService outerChannelDataService;

    public MonitorInnerServerHandler(Sender sender, MessageResolver resolver, AuthService authService, OuterChannelDataService outerChannelDataService) {
        super(sender, resolver);
        this.authService = authService;
        this.outerChannelDataService = outerChannelDataService;
    }

    @Override
    public void doSomethingAfterAllTransferChannelActive() {
        if (!isMonitorBrower) {
            log.info("Start monitor browser!");
            startMonitorBrowserAsync();
            isMonitorBrower = true;
        }
    }

    /**
     * 如果全部tansferChannel失效的话, 则需要释放和浏览器的连接,并且清空注册信息, 并且从OuterChannelDataService的list信息中删除对应channel信息.
     */
    @Override
    public void doSomethingAfterAllTransferChannelInactive() {
        log.info("All transfer channel disconnect, start clean work!");
        outerChannelDataService.clear();
        authService.clearRegisterAuthInfo();
    }

    private void startMonitorBrowserAsync() {
        CompletableFuture.runAsync(()-> {
            try {
                new TcpServer().startMonitor(Config.OUTER_REMOTE_PORT, new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        if (Config.READ_LIMIT != 0 || Config.WRITE_LIMIT != 0) {
                            pipeline.addLast(new GlobalTrafficShapingHandler(new NioEventLoopGroup(), Config.WRITE_LIMIT, Config.READ_LIMIT, 1000, 1000));
                        }
                        pipeline.addLast(new ByteArrayEncoder(), new ByteArrayDecoder(),
                                new MonitorBrowserHandler(sender, outerChannelDataService));
                    }
                }, MONITOR_BROWSER_BOSS_EVENTLOOP_GROUP, MONITOR_BROWSER_WORK_EVENTLOOP_GROUP);
            } catch (InterruptedException e) {
                log.info("monitor browser failed!");
                e.printStackTrace();
            }
        }, ThreadPoolUtil.getCommonExecutor());
    }
}
