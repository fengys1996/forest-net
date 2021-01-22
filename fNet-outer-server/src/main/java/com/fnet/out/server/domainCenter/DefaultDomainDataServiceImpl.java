package com.fnet.out.server.domainCenter;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author fys
 */
@Slf4j
@Component
public class DefaultDomainDataServiceImpl implements DomainDataService {

    /**
     * domain name ==== domain info
     */
    private final Map<String, DomainInfo> data = new HashMap<>(16);

    @Override
    public Map<String, DomainInfo> initData(List<String> domainList) {
        domainList.forEach(domainName -> {
            if (domainName != null && !domainName.isEmpty()) {
                data.put(domainName, new DomainInfo(domainName, ""));
            }
        });
        return Collections.unmodifiableMap(data);
    }

    @Override
    public DomainInfo issueDomain() {
        for (Entry<String, DomainInfo> domainInfoEntry : data.entrySet()) {
            DomainInfo domainInfo
                    = domainInfoEntry.getValue();
            if (domainInfo.isAvailable() && !domainInfo.isBindClient()) {
                log.info("issue: {}", domainInfo.getDomainName());
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
