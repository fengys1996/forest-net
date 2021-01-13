package com.fnet.common.config.cmd;

import com.fnet.common.config.Config;
import com.fnet.common.config.ConfigService;
import org.apache.commons.cli.*;

import static com.fnet.common.config.Config.*;

public class CmdConfigService implements ConfigService {
    @Override
    public void setInnerServerConfig(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("osa", true, "Outer server address!");
        options.addOption("osp", true, "Outer server port for monitor inner server!");

        options.addOption("rsp", true, "Real server port!");
        options.addOption("rsa", true, "Real server address!");

        options.addOption("pwd", true, "Password!");
        /**
         *  stop support multi-transfer
         *  options.addOption("nt", true, "Numbers of transfer channel");
         */
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {
            Config.OUTER_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("osp", String.valueOf(Config.DEFAULT_OUTER_SERVER_PORT)));
            Config.OUTER_SERVER_ADDRESS = commandLine.getOptionValue("osa", Config.DEFAULT_OUTER_SERVER_ADDRESS);

            Config.PASSWORD = commandLine.getOptionValue("pwd", Config.DEFAULT_PASSWORD);

            Config.REAL_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("rsp", String.valueOf(Config.DEFAULT_REAL_SERVER_PORT)));
            Config.REAL_SERVER_ADDRESS = commandLine.getOptionValue("rsa", Config.DEFAULT_REAL_SERVER_ADDRESS);
            // Config.TRANSFER_CHANNEL_NUMBERS = Integer.parseInt(commandLine.getOptionValue("nt", String.valueOf(DEFAULT_TRANSFER_CHANNEL_NUMBERS)));
            Config.TRANSFER_CHANNEL_NUMBERS = DEFAULT_TRANSFER_CHANNEL_NUMBERS;
        }
    }

    @Override
    public void setOuterServerConfig(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("port", true, "fNet Server port");
        options.addOption("password", true, "fNet Server password");
        options.addOption("remotePort", true, "Outer server port for monitor browser!");
        // options.addOption("nt", true, "The nums of transfer channel");
        options.addOption("wl", true, "write limit");
        options.addOption("rl", true, "read limit");
        options.addOption("dnl", true, "domain name list");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {
            Config.OUTER_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("port", String.valueOf(Config.DEFAULT_OUTER_SERVER_PORT)));
            Config.PASSWORD = commandLine.getOptionValue("password", Config.DEFAULT_PASSWORD);
            Config.OUTER_REMOTE_PORT = Integer.parseInt(commandLine.getOptionValue("remotePort", String.valueOf(Config.DEFAULT_OUTER_REMOTE_PORT)));
            // Config.TRANSFER_CHANNEL_NUMBERS = Integer.parseInt(commandLine.getOptionValue("nt", String.valueOf(Config.DEFAULT_TRANSFER_CHANNEL_NUMBERS)));
            Config.TRANSFER_CHANNEL_NUMBERS = DEFAULT_TRANSFER_CHANNEL_NUMBERS;
            Config.WRITE_LIMIT = Integer.parseInt(commandLine.getOptionValue("wl", String.valueOf(Config.DEFAULT_WRITE_LIMIT)));
            Config.READ_LIMIT = Integer.parseInt(commandLine.getOptionValue("rl", String.valueOf(Config.DEFAULT_READ_LIMIT)));
            Config.DOMAIN_NAME_LIST = commandLine.getOptionValue("dnl", DEFAULT_DOMAIN_NAME_LIST);
        }
    }
}
