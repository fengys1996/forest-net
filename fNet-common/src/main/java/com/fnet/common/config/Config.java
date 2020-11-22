package com.fnet.common.config;

import io.netty.util.internal.StringUtil;

public class Config {
    /**
     * default config
     */
    public static final int DEFAULT_OUTER_REMOTE_PORT = 8081;
    public static final int DEFAULT_OUTER_SERVER_PORT = 9091;
    public static final String DEFAULT_OUTER_SERVER_ADDRESS = "127.0.0.1";
    public static final String DEFAULT_PASSWORD = "12345678";
    public static final int DEFAULT_REAL_SERVER_PORT = 8080;
    public static final String DEFAULT_REAL_SERVER_ADDRESS = "127.0.0.1";

    public static int OUTER_REMOTE_PORT;
    public static int OUTER_SERVER_PORT;
    public static String OUTER_SERVER_ADDRESS;
    public static String PASSWORD;
    public static int REAL_SERVER_PORT;
    public static String REAL_SERVER_ADDRESS;

    public static boolean isInnerServerConfigComplete() {
        return !StringUtil.isNullOrEmpty(Config.OUTER_SERVER_ADDRESS) && !StringUtil.isNullOrEmpty(Config.PASSWORD) && Config.OUTER_SERVER_PORT != 0
                && Config.REAL_SERVER_PORT != 0 && !StringUtil.isNullOrEmpty(Config.REAL_SERVER_ADDRESS);
    }

    public static boolean isOuterServerConfigComplete() {
        return Config.OUTER_SERVER_PORT != 0 && !StringUtil.isNullOrEmpty(Config.PASSWORD) && Config.OUTER_REMOTE_PORT != 0;
    }
}
