package com.fnet.out.server.domainCenter;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

/**
 * @author fys
 */
public interface DomainDataService {

    Map<String, DomainInfo> initData(List<String> domainList) throws Exception;

    DomainInfo issueDomain();

    void recoveryDomainByTransferChannel(Channel channel);

    Channel getTransferChannelByDomainName(String domainName);
}
