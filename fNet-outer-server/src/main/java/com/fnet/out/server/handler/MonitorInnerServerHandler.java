package com.fnet.out.server.handler;

import com.fnet.common.config.Config;
import com.fnet.common.handler.AbstractMonitorHandler;
import com.fnet.common.net.TcpServer;
import com.fnet.common.service.Sender;
import com.fnet.common.service.ThreadPoolUtil;
import com.fnet.common.transfer.Resolver;
import com.fnet.out.server.service.AuthService;
import com.fnet.out.server.service.OuterChannelDataService;
import com.fnet.out.server.service.OuterSender;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

import static com.fnet.common.net.TcpServer.*;

@Slf4j
public class MonitorInnerServerHandler extends AbstractMonitorHandler {

    private static volatile boolean isMonitorBrower = false;

    public MonitorInnerServerHandler(Sender sender, Resolver resolver) {
        super(sender, resolver);
    }

    @Override
    public void doSomethingAfterAllTransferChannelActive() {
        if (!isMonitorBrower) {
            log.info("Start monitor browser!");
            startMonitorBrowser();
            isMonitorBrower = true;
        }
    }

    /**
     * 如果全部tansferChannel失效的话, 则需要释放和浏览器的连接,并且清空注册信息, 并且从OuterChannelDataService的list信息中删除对应channel信息.
     */
    @Override
    public void doSomethingAfterAllTransferChannelInactive() {
        log.info("All transfer channel disconnect, start clean work!");
        OuterChannelDataService.getInstance().clear();
        AuthService.getInstance().clearRegisterAuthInfo();
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
                                              new MonitorBrowserHandler(OuterSender.getInstance()));
                    }
                }, MONITOR_BROWSER_BOSS_EVENTLOOP_GROUP, MONITOR_BROWSER_WORK_EVENTLOOP_GROUP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
