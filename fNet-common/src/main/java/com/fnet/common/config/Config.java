package com.fnet.common.config;

import org.apache.commons.cli.Option;

import java.util.List;

public abstract class Config {

    abstract <T>T getOption(CmdConfigOption<T> cmdConfigOption);

    abstract <T>boolean setOption(CmdConfigOption<T> cmdConfigOption, T value);

    abstract List<Option> getOptions();
}
