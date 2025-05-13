package scheduler;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.logging.*;

public class CommandExecutor {
    private static final Path OUTPUT_PATH = Paths.get("sample-output.txt");
    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class.getName());
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    static {
        try {
            // Setting up the logger with a file handler and formatter

            LogManager.getLogManager().reset();
            FileHandler fileHandler = new FileHandler("execution.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            // Log setup failure

            System.err.println("Failed to set up logger: " + e.getMessage());
        }
    }

    public static void execute(String command, boolean isOneTime) {
        int attempt = 0;
        boolean success = false;
        // Retry loop for executing the command
        // with a maximum number of retries and delay between attempts
        while (attempt < MAX_RETRIES && !success) {
            attempt++;
            try {
                // Build and start the process

                ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
                Process process = builder.start();
                // Read the output and error streams

                String output = new String(process.getInputStream().readAllBytes());
                String error = new String(process.getErrorStream().readAllBytes());
                int exitCode = process.waitFor();

                // Format the output with timestamp and attempt details

                String timestamp = LocalDateTime.now().toString();
                String fullOutput = String.format("[%s] %sAttempt %d OUTPUT:\n%s\nERROR:\n%s\n\n",
                        timestamp,
                        isOneTime ? "[ONE-TIME] " : "[RECURRING] ",
                        attempt,
                        output.strip(),
                        error.strip());
                // Write the output to the specified file
                Files.writeString(OUTPUT_PATH, fullOutput, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                // Log the execution details
                LOGGER.info(String.format("Executed command (attempt %d): %s, Exit code: %d", attempt, command, exitCode));
                // Check if the command was successful

                success = (exitCode == 0);
                if (!success) LOGGER.warning("Retrying command due to non-zero exit code.");
                if (!success) Thread.sleep(RETRY_DELAY_MS);



            } catch (Exception e) {
                LOGGER.severe(String.format("Error executing command (attempt %d): %s", attempt, e.getMessage()));
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!success) {
            LOGGER.severe("Command failed after maximum retries: " + command);
        }
    }
}
