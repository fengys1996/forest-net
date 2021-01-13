package com.fnet.out.server.domainCenter;

import com.fnet.common.config.Config;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
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
    public Map<String, DomainInfo> data = new HashMap<>(16);

    @PostConstruct
    @Override
    public void initData() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostAddress = localHost.getHostAddress();

        String domainNameData = Config.DOMAIN_NAME_LIST;
        String[] domainNameList = domainNameData.split("\\*");
        for (String domainName : domainNameList) {
            data.put(domainName, new DomainInfo(domainName, hostAddress));
        }
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
