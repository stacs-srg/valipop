package utils;

import config.Config;
import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import model.simulationLogic.Simulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FileUtils {

    public static Logger log = LogManager.getLogger(FileUtils.class);

    public static PrintStream setupDatFileAsStream(String fileName, Config config) {

        PrintStream stream;

        try {

            File f = Paths.get("." + File.separator  + config.getSavePathDat().toString() + File.separator + fileName + ".dat").toAbsolutePath().normalize().toFile();
            stream = new PrintStream(f);

        } catch (IOException e) {
            log.info("Failed to set up summary results output stream - will output to standard out instead");
            stream = System.out;
        }

        return stream;
    }

    public static PrintStream setupDumpPrintStream(String fileName) {

        PrintStream stream;

        try {

            File f = Paths.get("." + File.separator  + "dump" + File.separator + fileName + ".txt").toAbsolutePath().normalize().toFile();
            stream = new PrintStream(f);

        } catch (IOException e) {
            log.info("Failed to set up summary results output stream - will output to standard out instead");
            stream = System.out;
        }

        return stream;
    }

    public static void outputFailureTimeTable(Collection<FailureTimeRow> failureTimeTable, PrintStream stream) {

        stream.println("years event group");

        for(FailureTimeRow r : failureTimeTable) {
            stream.println(r.toString());
        }
    }
}
