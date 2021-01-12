package com.fnet.out.server.task;

import com.fnet.common.config.Config;
import com.fnet.common.net.TcpServer;
import com.fnet.common.service.Sender;
import com.fnet.out.server.domainCenter.DomainDataService;
import com.fnet.out.server.handler.CheckHostHandler;
import com.fnet.out.server.handler.MonitorBrowserHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;

import static com.fnet.common.net.TcpServer.*;

@Slf4j
public class MonitorBrowserTask implements Runnable {
    Sender sender;
    DomainDataService domainDataService;

    public MonitorBrowserTask(Sender sender, DomainDataService domainDataService) {
        this.sender = sender;
        this.domainDataService = domainDataService;
    }

    private void startMonitorBrowserAsync() {
        try {
            new TcpServer().startMonitor(Config.OUTER_REMOTE_PORT, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (Config.READ_LIMIT != 0 || Config.WRITE_LIMIT != 0) {
                        pipeline.addLast(new GlobalTrafficShapingHandler(new NioEventLoopGroup(), Config.WRITE_LIMIT, Config.READ_LIMIT, 1000, 1000));
                    }
                    pipeline.addLast(new CheckHostHandler(domainDataService), new ByteArrayEncoder(), new ByteArrayDecoder(),
                                     new MonitorBrowserHandler(sender));
                }
            }, MONITOR_BROWSER_BOSS_EVENTLOOP_GROUP, MONITOR_BROWSER_WORK_EVENTLOOP_GROUP);
        } catch (InterruptedException e) {
            log.info("monitor browser failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startMonitorBrowserAsync();
    }
}
