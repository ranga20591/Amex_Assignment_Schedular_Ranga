package scheduler;

import java.time.LocalDateTime;

public class RecurringCommand {
    private final int interval;
    private final String command;

    public RecurringCommand(String line) {
        String[] parts = line.split(" ", 2);
        this.interval = Integer.parseInt(parts[0].substring(2));
        this.command = parts[1];
    }

    public boolean shouldRun(LocalDateTime now) {
        return now.getMinute() % interval == 0;
    }

    public String getCommand() {
        return command;
    }
}
