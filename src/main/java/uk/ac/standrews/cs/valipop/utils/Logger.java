package uk.ac.standrews.cs.valipop.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Logger {

    private static Path logFile;
    private Class callingClass;

    public Logger(Class callingClass) {
        this.callingClass = callingClass;
    }

    public static void setLogFilePath(Path logFilePath) {
        logFile = logFilePath;
    }

    public void log(String message) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        message = "[" + timestamp.toString() + "] " + callingClass.getSimpleName() + " | " + message + "\n";

        try {
            Files.write(logFile, message.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException | NullPointerException e) {
            System.out.print("Log setup has failed | " + message);
        }
    }

    public void info(String message) {
        log(message);
    }

    public void fatal(String message) {
        log(message);
    }

    public void error(String message) {
        log(message);
    }
}