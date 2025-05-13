# Scheduled Command Runner

This utility reads scheduled commands from a file and executes them based on the given schedule.

## Command Types

### One-Time Commands
Format:
```
Minute Hour Day Month Year <command>
```
Eg: 20 17 13 5 2025 date && echo "At Amex, We Do What's Right."


### Recurring Commands
Format:
```
*/n <command>
```
- Runs every `n` minutes.

- Eg:*/1 date && echo "Amex' motto is 'Don't live life without it!'"


## How to Run
1. Ensure Java 11+ is installed.
2. Add your commands to `/tmp/commands.txt`.
3. Run the app:
```sh
javac -d out src/scheduler/*.java
java -cp out scheduler.Main
```

## Assumptions
- Runs on Unix-like systems.
- Command file is well-formed.
