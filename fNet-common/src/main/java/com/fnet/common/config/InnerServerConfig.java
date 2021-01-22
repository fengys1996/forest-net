package com.fnet.common.config;


import io.netty.util.internal.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.Option;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.fnet.common.config.CmdConfigOption.*;

@Getter
@Setter
public class InnerServerConfig extends Config {
    private boolean hasHelp;

    private String osa;

    private int ospForInner;

    private String rsa;

    private int rsp;

    private String pwd;

    @Override
    public List<Option> getOptions() {
        List<Option> list = new ArrayList<>();
        list.add(HELP_OPTION);
        list.add(OUTER_SERVER_PORT_FOR_MONITOR_INNER_OPTION);
        list.add(PASSWORD_OPTION);
        list.add(OUTER_SERVER_ADDR_OPTION);
        list.add(REAL_SERVER_ADDR_OPTION);
        list.add(REAL_SERVER_PORT_OPTION);
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
        if (cmdConfigOption == PASSWORD_OPTION) {
            return (T) getPwd();
        }
        if (cmdConfigOption == OUTER_SERVER_ADDR_OPTION) {
            return (T) getOsa();
        }
        if (cmdConfigOption == REAL_SERVER_ADDR_OPTION) {
            return (T) getRsa();
        }
        if (cmdConfigOption == REAL_SERVER_PORT_OPTION) {
            return (T) Integer.valueOf(getOspForInner());
        }
        return null;
    }

    @Override
    public<T>boolean setOption(CmdConfigOption<T> cmdConfigOption, T value) {
        ObjectUtil.checkNotNull(cmdConfigOption, "config option");
        ObjectUtil.checkNotNull(value, "value");

        if (cmdConfigOption == HELP_OPTION) {
            setHasHelp((Boolean) value);
        } else if (cmdConfigOption == OUTER_SERVER_PORT_FOR_MONITOR_INNER_OPTION) {
            setOspForInner((Integer) value);
        } else if (cmdConfigOption == PASSWORD_OPTION) {
            setPwd((String) value);
        } else if (cmdConfigOption == OUTER_SERVER_ADDR_OPTION) {
            setOsa((String) value);
        } else if (cmdConfigOption == REAL_SERVER_ADDR_OPTION) {
            setRsa((String) value);
        } else if (cmdConfigOption == REAL_SERVER_PORT_OPTION) {
            setRsp((Integer) value);
        } else {
            return false;
        }
        return true;
    }
}
