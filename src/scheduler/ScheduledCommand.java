package scheduler;

import java.time.*;

public class ScheduledCommand {
    private final LocalDateTime scheduledTime;
    private final String command;
    private boolean executed = false;

    public ScheduledCommand(String line) {
        String[] parts = line.split(" ", 6);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid scheduled command format: " + line);
        }
        try {
            int minute = Integer.parseInt(parts[0]);
            int hour = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            int month = Integer.parseInt(parts[3]);
            int year = Integer.parseInt(parts[4]);
            this.scheduledTime = LocalDateTime.of(year, month, day, hour, minute);
        } catch (DateTimeException | NumberFormatException e) {
            throw new IllegalArgumentException("Invalid date/time in command: " + line, e);
        }
        this.command = parts[5];
    }

    public boolean shouldRun(LocalDateTime now) {
        if (!executed && now.getYear() == scheduledTime.getYear() &&
                now.getMonthValue() == scheduledTime.getMonthValue() &&
                now.getDayOfMonth() == scheduledTime.getDayOfMonth() &&
                now.getHour() == scheduledTime.getHour() &&
                now.getMinute() == scheduledTime.getMinute()) {
            executed = true;
            return true;
        }
        return false;
    }

    public String getCommand() {
        return command;
    }
}
