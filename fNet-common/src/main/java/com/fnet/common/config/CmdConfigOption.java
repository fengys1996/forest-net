package com.fnet.common.config;

import org.apache.commons.cli.Option;

import java.util.List;

/**
 * @author fys
 */
public class CmdConfigOption<T> extends Option {

    T defaultData;

    public T getDefaultData() {
        return defaultData;
    }

    public CmdConfigOption(String opt, boolean hasArg, String description, T defaultData) {
        super(opt, hasArg, description);
        this.defaultData = defaultData;
    }

    static final int DEFAULT_OUTER_SERVER_PORT_FOR_BROWSER = 8081;
    static final int DEFAULT_OUTER_SERVER_PORT_FOR_INNER = 9091;
    static final String DEFAULT_OUTER_SERVER_ADDRESS = "127.0.0.1";

    static final int DEFAULT_REAL_SERVER_PORT = 8080;
    static final String DEFAULT_REAL_SERVER_ADDRESS = "127.0.0.1";

    static final String DEFAULT_PASSWORD = "12345678";

    // ~~~~~~~~~~  common options ~~~~~~~~~~~~~

    public static final CmdConfigOption<String> HELP_OPTION = new CmdConfigOption<String>("h", false, "help", "false");

    public static final CmdConfigOption<Integer>
            OUTER_SERVER_PORT_FOR_MONITOR_INNER_OPTION = new CmdConfigOption<Integer>("osp_i", true, "outer server port for monitor inner server!",
                                                                                      DEFAULT_OUTER_SERVER_PORT_FOR_INNER);

    public static final CmdConfigOption<String> PASSWORD_OPTION = new CmdConfigOption<String>("pwd", true, "password", DEFAULT_PASSWORD);

    // ~~~~~~~~~~  inner server options ~~~~~~~~~~~~~

    public static final CmdConfigOption<String>
            OUTER_SERVER_ADDR_OPTION = new CmdConfigOption<String>("osa", true, "outer server address!", DEFAULT_OUTER_SERVER_ADDRESS);

    public static final CmdConfigOption<String>
            REAL_SERVER_ADDR_OPTION = new CmdConfigOption<String>("rsa", true, "real server address!", DEFAULT_REAL_SERVER_ADDRESS);

    public static final CmdConfigOption<Integer>
            REAL_SERVER_PORT_OPTION = new CmdConfigOption<Integer>("rsp", true, "real server port!", DEFAULT_REAL_SERVER_PORT);

    // ~~~~~~~~~~  outer server options ~~~~~~~~~~~~~

    public static final CmdConfigOption<Integer>
            OUTER_SERVER_PORT_FOR_MONITOR_BROWSER_OPTION = new CmdConfigOption<Integer>("osp_b", true, "outer server port for monitor inner server!",
                                                                                        DEFAULT_OUTER_SERVER_PORT_FOR_BROWSER);

    public static final CmdConfigOption<Integer>
            TOTAL_WRITE_LIMIT_OPTION = new CmdConfigOption<Integer>("twl", true, "total write limit(flow control)!", 0);

    public static final CmdConfigOption<Integer>
            TOTAL_READ_LIMIT_OPTION = new CmdConfigOption<Integer>("trl", true, "total read limit(flow control)!", 0);

    public static final CmdConfigOption<List<String>>
            DOMAIN_NAME_LIST_OPTION = new CmdConfigOption<List<String>>("dnl", true, "list of domain names bound to public network servers!", null);

    public static final CmdConfigOption<Integer> SO_RESUE_PORT_OPTION = new CmdConfigOption<Integer>("srp", true, "enable so_resueport, premise your environment is linux and kernel >= 3.9", 0);
}
