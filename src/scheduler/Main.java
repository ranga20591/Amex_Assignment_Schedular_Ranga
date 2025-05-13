package scheduler;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Configure logger
        configureLogger();

        String filePath = (args.length > 0) ? args[0] : "tmp/commands.txt";

        logger.info("Starting Command Scheduler with input file: " + filePath);

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            logger.severe("Command file not found: " + filePath);
            System.err.println("Error: Command file not found: " + filePath);
            return;
        }

        try {
            CommandScheduler scheduler = new CommandScheduler(filePath);
            scheduler.start();
            logger.info("Command Scheduler started successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start Command Scheduler", e);
        }
    }

    private static void configureLogger() {
        Logger rootLogger = Logger.getLogger("");
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addHandler(consoleHandler);
    }
}
