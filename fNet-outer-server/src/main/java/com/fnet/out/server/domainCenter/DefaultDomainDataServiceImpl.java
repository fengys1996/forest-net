package com.fnet.out.server.domainCenter;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author fys
 */
@Component
public class DefaultDomainDataServiceImpl implements DomainDataService {

    /**
     * domain name ==== domain info
     */
    public Map<String, DomainInfo> data = new HashMap<>(16);

    @PostConstruct
    @Override
    public void initData() {
        // use to local test
        // TODO: 域名信息初始化可以从配置文件获取
        String domainName = "localhost:8081";
        String domainName1 = "192.168.2.109:8081";
        data.put(domainName, new DomainInfo(domainName, "127.0.0.1"));
        data.put(domainName1, new DomainInfo(domainName1, "127.0.0.1"));
    }

    @Override
    public DomainInfo issueDomain() {
        for (Entry<String, DomainInfo> domainInfoEntry : data.entrySet()) {
            DomainInfo domainInfo
                    = domainInfoEntry.getValue();
            if (domainInfo.isAvailable() && !domainInfo.isBindClient()) {
                domainInfo.setAvailable(false);
                return domainInfo;
            }
        }
        return null;
    }

    @Override
    public void recoveryDomainByTransferChannel(Channel channel) {
        for (Entry<String, DomainInfo> domainInfoEntry : data.entrySet()) {
            DomainInfo domainInfo = domainInfoEntry.getValue();
            if (domainInfo.getTransferChannel() == channel) {
                domainInfo.reset();
            }
        }
    }

    @Override
    public Channel getTransferChannelByDomainName(String domainName) {
        DomainInfo domainInfo = data.get(domainName);
        if (domainInfo == null) {
            return null;
        }
        return domainInfo.getTransferChannel();
    }
}
