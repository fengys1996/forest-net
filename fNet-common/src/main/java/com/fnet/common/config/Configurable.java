package com.fnet.common.config;

public interface Configurable<T extends Config> {

    boolean initConfig(String[] args);

    boolean domeSomeSettingsAfterInitConfig();

    T config();
}
