package com.fnet.out.server.domainCenter;

import io.netty.channel.Channel;

public class DomainInfo {
    private String domainName;
    private String bindServerIp;
    private boolean isBindClient;
    private String bindClientIp;
    private boolean isAvailable;
    private Channel transferChannel;

    public DomainInfo(String domainName, String bindServerIp) {
        this.domainName = domainName;
        this.bindServerIp = bindServerIp;
        this.isBindClient = false;
        this.isAvailable = true;
    }

    public void reset() {
        this.isBindClient = false;
        this.bindClientIp = "";
        this.isAvailable = true;
        this.transferChannel = null;
    }

    public boolean isBindClient() {
        return isBindClient;
    }

    public void setBindClient(boolean bindClient) {
        isBindClient = bindClient;
    }

    public String getBindClientIp() {
        return bindClientIp;
    }

    public void setBindClientIp(String bindClientIp) {
        this.bindClientIp = bindClientIp;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getBindServerIp() {
        return bindServerIp;
    }

    public void setBindServerIp(String bindServerIp) {
        this.bindServerIp = bindServerIp;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Channel getTransferChannel() {
        return transferChannel;
    }

    public void setTransferChannel(Channel transferChannel) {
        this.transferChannel = transferChannel;
    }
}
