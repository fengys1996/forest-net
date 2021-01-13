package com.fnet.out.server.domainCenter;

import io.netty.channel.Channel;

import java.net.UnknownHostException;

/**
 * @author fys
 */
public interface DomainDataService {

    /**
     * 初始化Domain数据
     */
    void initData() throws Exception;

    /**
     * 颁发一个域名
     */
    DomainInfo issueDomain();

    /**
     * 回收一个域名
     */
    void recoveryDomainByTransferChannel(Channel channel);

    /**
     * 根据域名获取transfer channel
     */
    Channel getTransferChannelByDomainName(String domainName);
}
