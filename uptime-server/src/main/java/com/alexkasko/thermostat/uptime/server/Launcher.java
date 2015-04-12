package com.alexkasko.thermostat.uptime.server;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import ru.concerteza.springtomcat.etomcat8.EmbeddedTomcatStandalone;
import ru.concerteza.springtomcat.etomcat8.config.GeneralProperties;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Entry point for agent server example..
 */
public class Launcher {

    public static final File APP_ROOT = codeSourceDir(Launcher.class).getParentFile();
    private static final String HELP_OPTION = "help";
    private static final String VERSION_OPTION = "version";
    private static final String CONFIG_OPTION = "config";
    private static final Options OPTIONS = new Options()
            .addOption("h", HELP_OPTION, false, "show this page")
            .addOption("v", VERSION_OPTION, false, "show version")
            .addOption("c", CONFIG_OPTION, true, "config file");

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("agent.app_root", APP_ROOT.getPath());
        try {
            CommandLine cline = new GnuParser().parse(OPTIONS, args);
            if (cline.hasOption(VERSION_OPTION)) {
                System.out.println("1.0-SNAPSHOT");
            } else if (cline.hasOption(HELP_OPTION)) {
                throw new ParseException("Printing help page:");
            } else {
                System.out.println("Initializing application...");
                Properties props = loadProps(cline);
                EmbeddedTomcatStandalone et = new EmbeddedTomcatStandalone()
                        .setGeneralProps(new GeneralProperties()
                                .setPort(Integer.parseInt(props.getProperty("agent.tcp_port"))));
                et.start(APP_ROOT);
                while (true) {
                    Thread.sleep(5 * 60 * 1000);
//                    todo: enable "I am alive" logging
//                    logHeartbeat();
                }
            }
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            System.out.println(e.getMessage());
            System.out.println("1.0-SNAPSHOT");
            formatter.printHelp("java -jar ./bin/uptime-agent.jar [-c uptime-agent.conf]", OPTIONS);
        }
    }

    private static Properties loadProps(CommandLine cline) throws IOException {
        final File confFile;
        if (cline.hasOption(CONFIG_OPTION)) {
            confFile = new File(cline.getOptionValue(CONFIG_OPTION));
        } else {
            confFile = new File(APP_ROOT, "conf/uptime-agent.properties");
        }
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(confFile);
            InputStreamReader reader = new InputStreamReader(fis, Charset.forName("UTF-8"));
            props.load(reader);
            return props;
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    private static File codeSourceDir(Class<?> clazz) {
        try {
            URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
            File jarOrDir = new File(uri);
            return jarOrDir.isDirectory() ? jarOrDir : jarOrDir.getParentFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
