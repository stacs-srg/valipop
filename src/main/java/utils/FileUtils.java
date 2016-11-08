package utils;

import config.Config;
import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.LabelValueDataRow;
import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FileUtils {

    public static Logger log = LogManager.getLogger(FileUtils.class);

    private final static String SUMMARY_FILE_HEADINGS = "Start Time, Reason, Total Pop, Passed, Completed, " +
            "B Passes, MD Passes, FD Passes, P Passes, S Passes, MB Passes, " +
            "Sim Length, B Timestep, D Timestep, Input Width, " +
            "Start Pop, End Pop, Peak Pop, " +
            "Start Date, End Date" +
            "Run time, Results Directory";

    public static void makeDirectoryStructure(String runPurpose, String startTime, Config config) throws IOException {


        // check results dir exists
        Path results = config.getResultsSavePath();
        mkDirs(results);
        mkSummaryFile(results, "global-results-summary.csv");

        Path purpose = Paths.get(results.toString(), runPurpose);
        mkDirs(purpose);
        mkSummaryFile(purpose, runPurpose + "-results-summary.csv");



        // make folder named by startTime
        Path run = Paths.get(purpose.toString(), startTime);
        mkDirs(run);


        // initialise result file
        mkBlankFile(run, "detailed-results-" + startTime + ".txt");

        // make dat dir
        Path dat = Paths.get(run.toString(), "dat");
        mkDirs(dat);

        // make sub dirs of dat: birth, female-death, male-death, partnering, separation, multiple-birth
        EventType[] events = EventType.class.getEnumConstants();
        for(EventType e : events) {
            mkDirs(dat, e.toString());
        }

        // make dump dir
        mkDirs(run, "dump");
        // make population dir
        mkDirs(run, "population");

    }




    public static void main(String[] args) throws IOException {
        String dT = getDateTime();
        Config config = new Config(Paths.get(args[0]), "a", dT);
        makeDirectoryStructure("a", dT, config);
        makeDirectoryStructure("b", dT, config);
//        makeDirectoryStructure("double", config);

    }

    public static PrintStream setupDatFileAsStream(EventType event, String fileName, Config config) {

        PrintStream stream;

        try {

            File a = Paths.get(config.getResultsSavePath().toString(), config.getRunPurpose(), config.getStartTime(), "dat", event.toString(), fileName + ".dat").toFile();

//            File f = Paths.get("." + File.separator + config.getSavePathDat().toString() + File.separator + fileName + ".dat").toAbsolutePath().normalize().toFile();
            stream = new PrintStream(a);

        } catch (IOException e) {
            log.info("Failed to set up summary results output stream - will output to standard out instead");
            stream = System.out;
        }

        return stream;
    }

    public static PrintStream setupDumpPrintStream(String fileName, Config config) {

        PrintStream stream;

        try {

            File a = Paths.get(config.getResultsSavePath().toString(), config.getRunPurpose(), config.getStartTime(), "dump", fileName + ".txt").toFile();

//            File f = Paths.get("." + File.separator  + "dump" + File.separator + fileName + ".txt").toAbsolutePath().normalize().toFile();
            stream = new PrintStream(a);

        } catch (IOException e) {
            log.info("Failed to set up summary results output stream - will output to standard out instead");
            stream = System.out;
        }

        return stream;
    }

    public static void outputFailureTimeRowsToStream(Collection<FailureTimeRow> failureTimeRows, PrintStream stream) {

        stream.println("years event group");

        for(FailureTimeRow r : failureTimeRows) {
            stream.println(r.rowAsString());
        }

        stream.close();

    }

    public static void outputDataRowsToStream(String headings, Collection<LabelValueDataRow> dataRows, PrintStream stream) {

        if(headings.length() != 0) {
            stream.println(headings);
        }


        for(LabelValueDataRow r : dataRows) {
            stream.println(r.rowAsString());
        }
    }

    private static void mkBlankFile(Path parent, String fileName) throws IOException {

        Path summary = Paths.get(parent.toString(), fileName);

        if(!Files.exists(summary)) {
            // if not, initialise summary file with headings

            try {
                PrintWriter write = new PrintWriter(summary.toFile(), "UTF-8");
                write.close();
            } catch (IOException e) {
                String m = "Unable to create to " + summary.toString();
                log.fatal(m);
                throw new IOException(m, e);
            }
        }

    }

    private static void mkSummaryFile(Path parent, String fileName) throws IOException {
        // Check if summary file exists
        Path summary = Paths.get(parent.toString(), fileName);

        if(!Files.exists(summary)) {
            // if not, initialise summary file with headings

            try {
                PrintWriter write = new PrintWriter(summary.toFile(), "UTF-8");
                write.println(SUMMARY_FILE_HEADINGS);
                write.close();
            } catch (IOException e) {
                String m = "Unable to create or write to " + summary.toString();
                log.fatal(m);
                throw new IOException(m, e);
            }
        }
    }

    public static String getDateTime() {
        DateFormat dF = new SimpleDateFormat("yyyyMMdd-HHmmss:SSS");
        return dF.format(Calendar.getInstance().getTime());
    }

    private static boolean mkDirs(Path parent, String newDir) {

        Path path = Paths.get(parent.toString(), newDir);
        return mkDirs(path);

    }

    private static boolean mkDirs(Path path) {
        if(!Files.exists(path)) {
            // if not make one
            return new File(path.toString()).mkdirs();
        }
        return true;
    }

}
