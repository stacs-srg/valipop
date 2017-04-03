package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CustomLog4j {

    public static Logger setup(Path logFile, Object logOwner) {

        System.setProperty("logFilename", logFile.toString());
        return LogManager.getLogger(logOwner.getClass());

    }

}
