/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence;

import org.geoserver.csv2geofence.config.model.internal.RunInfo;
import java.io.*;
import java.util.Iterator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Application main file.
 *
 * Parses the input params and performs some validations on them.
 */
public class Cvs2Xml {

    private final static Logger LOGGER = LogManager.getLogger(Cvs2Xml.class);

    protected static final char CLI_CONFIGFILE_CHAR = 'c';
    protected static final char CLI_USERFILE_CHAR = 'u';
    protected static final char CLI_RULEFILE_CHAR = 'r';
    protected static final char CLI_OUTPUTFILE_CHAR = 'o';
    protected static final char CLI_SEND_CHAR = 's';

//    protected static final String CLI_ALIGNGROUPS_LONG = "aligngroups";
    protected static final String CLI_DELETERULES_LONG = "deleterules";
    protected static final String CLI_DELETERULES_CHAR = "d";

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        LOGGER.info("Running " + Cvs2Xml.class.getSimpleName());

        RunInfo runInfo = parse(args);
        if( ! validate(runInfo))
            return;
        Runner runner = new Runner(runInfo);
        runner.run();
    }

    protected static RunInfo parse(String[] args) {

        Options options = createCLIOptions();

        if (isHelpRequested(args)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Cvs2Xml.class.getSimpleName(), options);
            System.exit(0);
        }

        CommandLineParser parser = new PosixParser();
        CommandLine cli = null;
        try {
            cli = parser.parse(options, args);
        } catch (ParseException ex) {
            LOGGER.warn(ex.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Cvs2Xml.class.getSimpleName(), options);
            
            System.exit(1);
        }

        RunInfo runInfo = new RunInfo();

        String cfgFileName = cli.getOptionValue(CLI_CONFIGFILE_CHAR);
        LOGGER.info("Config file is " + cfgFileName);
        runInfo.setConfigurationFile(new File(cfgFileName));

        String[] userFiles = cli.getOptionValues(CLI_USERFILE_CHAR);
        String[] ruleFiles = cli.getOptionValues(CLI_RULEFILE_CHAR);

        // just print out the full list of input files
        if(userFiles != null) {
            LOGGER.info("Requested user definition files:");
            for (String userFile : userFiles) {
                LOGGER.info(" user file '" + userFile+"'");
                runInfo.getUserFiles().add(new File(userFile));
            }
        }
        else
            LOGGER.info("No user definition file");

        if(ruleFiles != null) {
            LOGGER.info("Requested rule definition files:");
            for (String ruleFile : ruleFiles) {
                LOGGER.info(" rule file '" + ruleFile+"'");
                runInfo.getRuleFiles().add(new File(ruleFile));
            }
        }
        else
            LOGGER.info("No rule definition file");


        final String xmlOutputFileName = cli.getOptionValue(CLI_OUTPUTFILE_CHAR);
        if(xmlOutputFileName != null) {
            File xmlWriterFile = new File(xmlOutputFileName);
            runInfo.setOutputFile(xmlWriterFile);
        }

        runInfo.setSendRequested(cli.hasOption(CLI_SEND_CHAR));

//        if(cli.hasOption(CLI_ALIGNGROUPS_LONG))
//            runInfo.setGroupAlignRequested(true);

        if(cli.hasOption(CLI_DELETERULES_LONG))
            runInfo.setDeleteObsoleteRules(true);

        return runInfo;
    }


    protected static boolean validate(RunInfo runInfo) {

        // load configuration file
        File configFile = runInfo.getConfigurationFile();
        if( ! configFile.exists() || ! configFile.isFile() || ! configFile.canRead()) {
            LOGGER.error("Can't read configuration file " + configFile);
            return false;
        }
        
        if(runInfo.getOutputFile() != null) {            
            if( ! runInfo.getOutputFile().getAbsoluteFile().getParentFile().canWrite() ) {
                LOGGER.error("Can't write xml command file " + runInfo.getOutputFile().getAbsolutePath());
                return false;
            }
        }

        for (Iterator it = runInfo.getUserFiles().iterator(); it.hasNext();) {
            File userFile = (File)it.next();
            if( ! userFile.exists() || ! userFile.isFile() || ! userFile.canRead()) {
                LOGGER.error("Can't read user file " + userFile.getAbsolutePath() + ". Skipping file.");
                it.remove();
            }
        }

        for (Iterator it = runInfo.getRuleFiles().iterator(); it.hasNext();) {
            File ruleFile = (File)it.next();
            if( ! ruleFile.exists() || ! ruleFile.isFile() || ! ruleFile.canRead()) {
                LOGGER.error("Can't read rule file " + ruleFile + ". Skipping file.");
                it.remove();
            }
        }

        if(runInfo.getUserFiles().size() + runInfo.getRuleFiles().size() == 0) {
            LOGGER.error("Neither user or rule file to process. Skipping out.");
            return false;
        }

        if(runInfo.getOutputFile() == null && ! runInfo.isSendRequested()) {
            LOGGER.error("Neither output file or send to geofence was requested. Skipping out.");
            return false;
        }

        return true;

    }

    protected static boolean isHelpRequested(String[] args) {
        for (String arg : args) {
            if("-h".equals(arg) || "--help".equals(arg))
                return true;
        }
        return false;
    }


    protected static Options createCLIOptions() throws IllegalArgumentException {
        // create Options object
        Options options = new Options();
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArg()
                .withDescription("the XML configuration file")
                .isRequired()
                .withLongOpt("configfile")
                .create(CLI_CONFIGFILE_CHAR));
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArgs()
                .withDescription("the CSV user/groups file (0 or more)")
                .withLongOpt("userFile")
                .create(CLI_USERFILE_CHAR));
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArgs()
                .withDescription("the CSV groups/rules file (0 or more)")
                .withLongOpt("ruleFile")
                .create(CLI_RULEFILE_CHAR));
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArgs()
                .withDescription("the output XML GeoFence command")
                .withLongOpt("output")
                .create(CLI_OUTPUTFILE_CHAR));
        options.addOption(OptionBuilder
                .withDescription("Send commands to GeoFence")
                .withLongOpt("send")
                .create(CLI_SEND_CHAR));
//        options.addOption(OptionBuilder
//                .withDescription("Create groups if they don't exist")
////                .withLongOpt("send")
//                .create(CLI_ALIGNGROUPS_LONG));
        options.addOption(OptionBuilder
                .withDescription("Delete obsolete rules")
                .withLongOpt(CLI_DELETERULES_LONG)
                .create(CLI_DELETERULES_CHAR));
        return options;
    }

}
