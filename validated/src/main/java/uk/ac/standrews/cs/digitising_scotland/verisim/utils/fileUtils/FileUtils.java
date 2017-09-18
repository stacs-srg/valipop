/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils;

import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.simulationSummaryLogging.SummaryRow;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FileUtils {

    private static Logger log = null;

    private static Path globalSummaryPath;
    private static Path resultsSummaryPath;
    private static Path detailedResultsPath;
    private static Path tracePath;
    private static Path recordsPath;
    private static Path contingencyTablesPath;

    private static void checkLogFile() {
        if(log == null) {
            log = LogManager.getLogger(FileUtils.class);
        }
    }

    public static void makeDirectoryStructure(String runPurpose, String startTime, String resultPath) throws IOException {


        // check results dir exists
        Path results = Paths.get(resultPath);
        mkDirs(results);

        globalSummaryPath = mkSummaryFile(results, "global-results-summary.csv");

        Path purpose = Paths.get(results.toString(), runPurpose);
        mkDirs(purpose);
        resultsSummaryPath = mkSummaryFile(purpose, runPurpose + "-results-summary.csv");

        // make folder named by startTime
        Path run = Paths.get(purpose.toString(), startTime);
        mkDirs(run);

        // initialise result file
        detailedResultsPath = mkBlankFile(run, "detailed-results-" + startTime + ".txt");

        // make dump dir
        mkDirs(run, "dump");

        // make population dir
        recordsPath = Paths.get(run.toString(), "records");
        mkDirs(recordsPath);

        contingencyTablesPath = Paths.get(run.toString(), "tables");
        mkDirs(contingencyTablesPath);

        Path log = Paths.get(run.toString(), "log");
        mkDirs(log);
        tracePath = mkBlankFile(log, "trace.txt");

    }

    public static PrintStream setupDumpPrintStream(String fileName, Config config) {

        PrintStream stream;

        try {

            File a = Paths.get(config.getResultsSavePath().toString(), config.getRunPurpose(), config.getStartTime(), "dump", fileName + ".txt").toFile();
            stream = new PrintStream(a, "UTF-8");

        } catch (IOException e) {
            checkLogFile();
            log.info("Failed to set up summary results output stream - will output to standard out instead");
            stream = System.out;
        }

        return stream;
    }

    public static Path mkBlankFile(Path parent, String fileName) throws IOException {

        Path blankFilePath = Paths.get(parent.toString(), fileName);

        if(!Files.exists(blankFilePath)) {
            // if not, initialise summary file with headings

            try {
                PrintWriter write = new PrintWriter(blankFilePath.toFile(), "UTF-8");
                write.close();
            } catch (IOException e) {
                String m = "Unable to create to " + blankFilePath.toString();
                throw new IOException(m, e);
            }
        }

        return blankFilePath;

    }

    private static Path mkSummaryFile(Path parent, String fileName) throws IOException {
        // Check if summary file exists
        Path summary = Paths.get(parent.toString(), fileName);

        if(!Files.exists(summary)) {
            // if not, initialise summary file with headings

            try {
                PrintWriter write = new PrintWriter(summary.toFile(), "UTF-8");
                write.println(SummaryRow.getSeparatedHeadings(','));
                write.close();
            } catch (IOException e) {
                String m = "Unable to create or write to " + summary.toString();
                throw new IOException(m, e);
            }
        }

        return summary;
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

    public static Path getGlobalSummaryPath() {
        return globalSummaryPath;
    }

    public static Path getDetailedResultsPath() {
        return detailedResultsPath;
    }

    public static Path getTracePath() {
        return tracePath;
    }

    public static Path getRecordsDirPath() {
        return recordsPath;
    }

    public static Path getContingencyTablesPath() {
        return contingencyTablesPath;
    }

    public static Path getResultsSummaryPath() {
        return resultsSummaryPath;
    }

    public static Path pathToLogDir(String runPurpose, String startTime, String resultPath) {
        return Paths.get(resultPath, runPurpose, startTime, "log", "trace.txt");
    }

    public static void writeSummaryRowToSummaryFiles(SummaryRow row) throws IOException {
        Files.write(globalSummaryPath, row.toSeperatedString(',').getBytes(), StandardOpenOption.APPEND);
        Files.write(resultsSummaryPath, row.toSeperatedString(',').getBytes(), StandardOpenOption.APPEND);
    }

    public static String getDateTime() {
        DateFormat dF = new SimpleDateFormat("yyyyMMdd-HHmmss:SSS");
        return dF.format(Calendar.getInstance().getTime());
    }

}
