package com.fnet.common.config;

public interface ConfigService {

    void setInnerServerConfig(String[] args) throws Exception;

    void setOuterServerConfig(String[] args) throws Exception;
}
