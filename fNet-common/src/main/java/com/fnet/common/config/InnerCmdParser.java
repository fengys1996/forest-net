package com.fnet.common.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import static com.fnet.common.config.CmdConfigOption.*;

public class InnerCmdParser extends CmdParser<InnerServerConfig> {

    public InnerCmdParser(String[] cmd) {
        super(cmd);
    }

    @Override
    public InnerServerConfig parse() throws ParseException {
        InnerServerConfig innerServerConfig = new InnerServerConfig();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        innerServerConfig.getOptions().forEach(option -> options.addOption(option));
        CommandLine commandLine = parser.parse(options, cmd);

        if (commandLine.hasOption(HELP_OPTION.getOpt())) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
            return null;
        }

        typeConversionAndSetConfig(innerServerConfig, commandLine, PASSWORD_OPTION, false, DataType.IS_STRING);
        typeConversionAndSetConfig(innerServerConfig, commandLine, OUTER_SERVER_ADDR_OPTION, false, DataType.IS_STRING);
        typeConversionAndSetConfig(innerServerConfig, commandLine, OUTER_SERVER_PORT_FOR_MONITOR_INNER_OPTION, false, DataType.IS_INTEGER);
        typeConversionAndSetConfig(innerServerConfig, commandLine, REAL_SERVER_ADDR_OPTION, false, DataType.IS_STRING);
        typeConversionAndSetConfig(innerServerConfig, commandLine, REAL_SERVER_PORT_OPTION, false, DataType.IS_INTEGER);

        return innerServerConfig;
    }
}
