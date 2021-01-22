package com.fnet.common.config;


import io.netty.util.internal.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.Option;

import java.util.ArrayList;
import java.util.List;

import static com.fnet.common.config.CmdConfigOption.*;

@Getter
@Setter
public class OuterServerConfig extends Config {
    private boolean hasHelp;

    private int ospForInner;

    private int ospForBrowser;

    private int writeLimit;

    private int readLimit;

    private List<String> domainNameList;

    private int enableSoReusePort;

    private String pwd;

    @Override
    public List<Option> getOptions() {
        List<Option> list = new ArrayList<>();
        list.add(HELP_OPTION);
        list.add(OUTER_SERVER_PORT_FOR_MONITOR_INNER_OPTION);
        list.add(OUTER_SERVER_PORT_FOR_MONITOR_BROWSER_OPTION);
        list.add(TOTAL_WRITE_LIMIT_OPTION);
        list.add(TOTAL_READ_LIMIT_OPTION);
        list.add(DOMAIN_NAME_LIST_OPTION);
        list.add(SO_RESUE_PORT_OPTION);
        list.add(PASSWORD_OPTION);
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T>T getOption(CmdConfigOption<T> cmdConfigOption) {
        ObjectUtil.checkNotNull(cmdConfigOption, "config option");

        if (cmdConfigOption == HELP_OPTION) {
            return (T) Boolean.valueOf(isHasHelp());
        }
        if (cmdConfigOption == OUTER_SERVER_PORT_FOR_MONITOR_INNER_OPTION) {
            return (T) Integer.valueOf(getOspForInner());
        }
        if (cmdConfigOption == OUTER_SERVER_PORT_FOR_MONITOR_BROWSER_OPTION) {
            return (T) Integer.valueOf(getOspForBrowser());
        }
        if (cmdConfigOption == TOTAL_WRITE_LIMIT_OPTION) {
            return (T) Integer.valueOf(getWriteLimit());
        }
        if (cmdConfigOption == TOTAL_READ_LIMIT_OPTION) {
            return (T) Integer.valueOf(getReadLimit());
        }
        if (cmdConfigOption == DOMAIN_NAME_LIST_OPTION) {
            return (T) getDomainNameList();
        }
        if (cmdConfigOption == SO_RESUE_PORT_OPTION) {
            return (T) Integer.valueOf(getEnableSoReusePort());
        }
        if (cmdConfigOption == PASSWORD_OPTION) {
            return (T) getPwd();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public<T>boolean setOption(CmdConfigOption<T> cmdConfigOption, T value) {
        ObjectUtil.checkNotNull(cmdConfigOption, "config option");
        ObjectUtil.checkNotNull(value, "value");

        if (cmdConfigOption == HELP_OPTION) {
            setHasHelp((Boolean) value);
        } else if (cmdConfigOption == OUTER_SERVER_PORT_FOR_MONITOR_INNER_OPTION) {
            setOspForInner((Integer) value);
        } else if (cmdConfigOption == OUTER_SERVER_PORT_FOR_MONITOR_BROWSER_OPTION) {
            setOspForBrowser((Integer) value);
        } else if (cmdConfigOption == TOTAL_WRITE_LIMIT_OPTION) {
            setWriteLimit((Integer) value);
        } else if (cmdConfigOption == TOTAL_READ_LIMIT_OPTION) {
            setReadLimit((Integer) value);
        } else if (cmdConfigOption == DOMAIN_NAME_LIST_OPTION) {
            setDomainNameList((List<String>) value);
        } else if (cmdConfigOption == SO_RESUE_PORT_OPTION) {
            setEnableSoReusePort((Integer) value);
        } else if (cmdConfigOption == PASSWORD_OPTION) {
            setPwd((String) value);
        } else {
            return false;
        }
        return true;
    }
}
