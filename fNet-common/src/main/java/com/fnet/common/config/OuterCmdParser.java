package com.fnet.common.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import static com.fnet.common.config.CmdConfigOption.*;
import static com.fnet.common.config.CmdParser.DataType.*;

public class OuterCmdParser extends CmdParser<OuterServerConfig> {

    public OuterCmdParser(String[] cmd) {
        super(cmd);
    }

    @Override
    public OuterServerConfig parse() throws ParseException {
        OuterServerConfig outerServerConfig = new OuterServerConfig();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        outerServerConfig.getOptions().forEach(option -> options.addOption(option));
        CommandLine commandLine = parser.parse(options, cmd);

        if (commandLine.hasOption(HELP_OPTION.getOpt())) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
            return null;
        }

        typeConversionAndSetConfig(outerServerConfig, commandLine, PASSWORD_OPTION, false, DataType.IS_STRING);
        typeConversionAndSetConfig(outerServerConfig, commandLine, OUTER_SERVER_PORT_FOR_MONITOR_INNER_OPTION, false, IS_INTEGER);
        typeConversionAndSetConfig(outerServerConfig, commandLine, OUTER_SERVER_PORT_FOR_MONITOR_BROWSER_OPTION, false, IS_INTEGER);
        typeConversionAndSetConfig(outerServerConfig, commandLine, TOTAL_WRITE_LIMIT_OPTION, false, IS_INTEGER);
        typeConversionAndSetConfig(outerServerConfig, commandLine, TOTAL_READ_LIMIT_OPTION, false, IS_INTEGER);
        typeConversionAndSetConfig(outerServerConfig, commandLine, DOMAIN_NAME_LIST_OPTION, true, IS_STRING);
        typeConversionAndSetConfig(outerServerConfig, commandLine, SO_RESUE_PORT_OPTION, false, IS_INTEGER);

        return outerServerConfig;
    }
}
