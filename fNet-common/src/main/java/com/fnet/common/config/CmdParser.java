package com.fnet.common.config;

import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fys
 */
public abstract class CmdParser<T extends Config> {

    private static final String SEPARATOR = "\\*";
    protected String[] cmd;

    protected CmdParser(String[] cmd) {
        this.cmd = cmd.clone();
    }

    abstract T parse() throws Exception;

    enum DataType {
        IS_INTEGER,
        IS_STRING
    }

    @SuppressWarnings("unchecked")
    protected <Y> void typeConversionAndSetConfig(T config, CommandLine commandLine, CmdConfigOption<Y> cmdConfigOption, boolean isList, DataType dataType){
        String defaultValue = String.valueOf(cmdConfigOption.getDefaultData());
        String optionValue = commandLine.getOptionValue(cmdConfigOption.getOpt(), defaultValue == "null" ? "" : defaultValue);
        Object result = null;

        if (dataType == DataType.IS_INTEGER) {
            if (!isList)    result = Integer.valueOf(optionValue);
            else {
                result = splitToIntegerList(optionValue);
            }
        }
        if (dataType == DataType.IS_STRING) {
            if (!isList)     result = optionValue;
            else {
                result = splitToStringList(optionValue);
            }
        }
        config.setOption(cmdConfigOption, (Y)result);
    }

    private List<String> splitToStringList(String conent) {
        String[] split = conent.split(SEPARATOR);
        return new ArrayList<>(Arrays.asList(split));
    }

    private List<Integer> splitToIntegerList(String conent) {
        List<Integer> list = new ArrayList<>(5);
        String[] split = conent.split(SEPARATOR);
        for (String s : split) {
            list.add(Integer.valueOf(s));
        }
        return list;
    }
}


