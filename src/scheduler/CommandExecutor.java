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
            LogManager.getLogManager().reset();
            FileHandler fileHandler = new FileHandler("execution.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to set up logger: " + e.getMessage());
        }
    }

    public static void execute(String command, boolean isOneTime) {
        int attempt = 0;
        boolean success = false;

        while (attempt < MAX_RETRIES && !success) {
            attempt++;
            try {
                ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
                Process process = builder.start();

                String output = new String(process.getInputStream().readAllBytes());
                String error = new String(process.getErrorStream().readAllBytes());
                int exitCode = process.waitFor();

                String timestamp = LocalDateTime.now().toString();
                String fullOutput = String.format("[%s] %sAttempt %d OUTPUT:\n%s\nERROR:\n%s\n\n",
                        timestamp,
                        isOneTime ? "[ONE-TIME] " : "[RECURRING] ",
                        attempt,
                        output.strip(),
                        error.strip());

                Files.writeString(OUTPUT_PATH, fullOutput, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                LOGGER.info(String.format("Executed command (attempt %d): %s, Exit code: %d", attempt, command, exitCode));
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
