package scheduler;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class CommandScheduler {
    private final String filePath;
    private final List<ScheduledCommand> oneTimeCommands = new ArrayList<>();
    private final List<RecurringCommand> recurringCommands = new ArrayList<>();

    public CommandScheduler(String filePath) throws IOException {
        this.filePath = filePath;
        loadCommands();
    }

    private void loadCommands() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            try {
                if (line.trim().isEmpty()) continue;
                if (line.startsWith("*/")) {
                    recurringCommands.add(new RecurringCommand(line));
                } else {
                    oneTimeCommands.add(new ScheduledCommand(line));
                }
            } catch (Exception e) {
                System.err.println("Invalid command format: " + line);
            }
        }
    }

    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            Iterator<ScheduledCommand> iterator = oneTimeCommands.iterator();
            while (iterator.hasNext()) {
                ScheduledCommand cmd = iterator.next();
                if (cmd.shouldRun(now)) {
                    CommandExecutor.execute(cmd.getCommand(), true);
                    iterator.remove(); // ensure it runs only once
                }
            }
            for (RecurringCommand cmd : recurringCommands) {
                if (cmd.shouldRun(now)) {
                    CommandExecutor.execute(cmd.getCommand(), false);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
//adding a shutdown Hook for gracefull Shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }));
    }
}
