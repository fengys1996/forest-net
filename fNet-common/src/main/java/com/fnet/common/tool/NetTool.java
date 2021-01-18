package com.fnet.common.tool;

/**
 * @author fys
 */
public class NetTool {

    public static boolean isLinuxEnvironment() {
        String osName = System.getProperties().getProperty("os.name").toLowerCase();
        return osName.contains("linux");
    }
}
