package scheduler;

import java.time.LocalDateTime;

public class ScheduledCommand {
    private final int minute, hour, day, month, year;
    private final String command;
    private boolean executed = false;

    public ScheduledCommand(String line) {
        String[] parts = line.split(" ", 6);
        this.minute = Integer.parseInt(parts[0]);
        this.hour = Integer.parseInt(parts[1]);
        this.day = Integer.parseInt(parts[2]);
        this.month = Integer.parseInt(parts[3]);
        this.year = Integer.parseInt(parts[4]);
        this.command = parts[5];
    }

    public boolean shouldRun(LocalDateTime now) {
        return !executed &&
               now.getMinute() == minute &&
               now.getHour() == hour &&
               now.getDayOfMonth() == day &&
               now.getMonthValue() == month &&
               now.getYear() == year;
    }

    public String getCommand() {
        executed = true;
        return command;
    }
}
