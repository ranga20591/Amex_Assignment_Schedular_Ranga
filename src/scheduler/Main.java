package scheduler;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = "tmp/commands.txt";
        CommandScheduler scheduler = new CommandScheduler(filePath);
        scheduler.start();
    }
}
