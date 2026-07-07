/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * For extracting, invoking, and reading the results of the R analysis scripts.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 * @author Daniel Brathagen (db255@st-andrews.ac.uk)
 */
public class ContingencyTableValidator {

    // TODO add option generate contingency tables from config file if not already present.

    private static final String CONTINGENCY_TABLES_DIRECTORY_NAME = "tables";
    private static final String ANALYSIS_SCRIPT_FILENAME = "analysis.R";
    private static final String ANALYSIS_OUTPUT_FILENAME = "analysis.out";

    private static final int NORMAL_EXIT_CODE = 0;
    private static final int R_PROCESS_TIMEOUT_IN_MINUTES = 10;

    // Relative to src/main/resources.
    private static final Path ANALYSIS_SCRIPT_SOURCE_DIRECTORY = Path.of("valipop/analysis-r/geeglm/");

    private static final boolean DELETE_ANALYSIS_OUTPUT_FILE = true;

    private final Path workingDirectoryPath;
    private final int maxMotherBirthAge;
    private final int startYear;
    private final int endYear;

    public ContingencyTableValidator(final Path workingDirectoryPath, final int maxMotherBirthAge, final int startYear, final int endYear) {

        this.workingDirectoryPath = workingDirectoryPath;
        this.maxMotherBirthAge = maxMotherBirthAge;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public synchronized double getValidationScore() throws IOException {

        // Synchronized since method creates and deletes files in the parent directory.

        final Path analysisScriptSource = ANALYSIS_SCRIPT_SOURCE_DIRECTORY.resolve(ANALYSIS_SCRIPT_FILENAME);
        final Path analysisScriptTempCopy = workingDirectoryPath.resolve(ANALYSIS_SCRIPT_FILENAME);
        final Path contingencyTablesDirectoryPath = workingDirectoryPath.resolve(CONTINGENCY_TABLES_DIRECTORY_NAME);
        final Path analysisOutputFile = workingDirectoryPath.resolve(ANALYSIS_OUTPUT_FILENAME);

        makeTempCopyOfRScript(analysisScriptSource, analysisScriptTempCopy);

        final Process process = runRScript(contingencyTablesDirectoryPath, analysisScriptTempCopy, maxMotherBirthAge, startYear, endYear);
        final double score = getRScriptResult(process, analysisOutputFile);

        try {
            checkExitStatus(process);
        }
        finally {
            Files.delete(analysisScriptTempCopy);
            if (DELETE_ANALYSIS_OUTPUT_FILE) Files.delete(analysisOutputFile);
        }

        return score;
    }

    private static void checkExitStatus(final Process process) {

        try {
            final boolean timedOut = !process.waitFor(R_PROCESS_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);

            if (timedOut || process.exitValue() != NORMAL_EXIT_CODE)
                throw new RuntimeException("Execution of analysis script failed");
        }
        catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Make a temporary copy of the R analysis script in the local directory. Needed in the case of running from
     * a jar, and harmless otherwise.
     */
    private static void makeTempCopyOfRScript(final Path analysisScriptSource, final Path analysisScriptTempCopy) throws IOException {

        try (
            // Retrieve the R file as a stream in case it's in a jar.
            final InputStream stream = ContingencyTableValidator.class.getClassLoader().getResourceAsStream(analysisScriptSource.toString());
            final OutputStream output = new FileOutputStream(analysisScriptTempCopy.toFile())
        ) {
            IOUtils.copy(stream, output);
        }
    }

    /**
     * Executes the R analysis script and returns the running process.
     * 
     * @param runDirPath the path of the current run directory
     * @param rScriptPath the path of the R analysis script
     * @param maxMotherBirthAge the maximum birthing age of the population model
     * 
     * @return the executing process
     */
    private static Process runRScript(final Path runDirPath, final Path rScriptPath, final int maxMotherBirthAge, final int startYear, final int endYear) throws IOException {

        final String[] command = { "Rscript", rScriptPath.toString(), runDirPath.toAbsolutePath().toString(), String.valueOf(maxMotherBirthAge), String.valueOf(startYear), String.valueOf(endYear)};

        final ProcessBuilder builder = new ProcessBuilder(command);
        return builder.start();
    }

    /**
     * Outputs the standard output and error of the R analysis to {@code outputPath} and returns the calculated v value.
     * 
     * @param process the executing R analysis process
     * @param outputPath the path of the process output and error streams
     */
    private static double getRScriptResult(final Process process, final Path outputPath) throws IOException {

        // Extracting stdout and stderr
        final BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // The file the output of the R script is written to
        final File outputFile = new File(outputPath.toString());
        final FileWriter outputFileWriter = new FileWriter(outputFile, false);
        outputFile.createNewFile();

        // Filter relevant lines, calculate v per line and sum together
        final int v = stdout.lines()
            // Writing lines to file
            .filter(ContingencyTableValidator::filterAnalysis)
            .peek((l) -> {
                try {
                    outputFileWriter.write(l);
                    outputFileWriter.append("\n");
                } catch (final IOException e) {
                    System.err.println("Unable to write results of analysis to file " + outputPath);
                }
            })
            .filter(ContingencyTableValidator::filterAnalysis)
            .map(ContingencyTableValidator::getLineScore)
            .reduce(Double::sum)
            .map((res) -> (int) Math.floor(res))
            .orElse(0);

        // Print out any errors
        stderr.lines().forEach(System.err::println);

        // Clean up
        stdout.close();
        stderr.close();
        outputFileWriter.close();

        return v;
    }

    private static boolean filterAnalysis(final String line) {

        // Only interested in interactions with STATS.
        return line.contains("STAT");
    }

    // TODO define alternative metric including normalisation for number of interactions.
    private static double getLineScore(final String line) {

        final int MAX_STARS = 3;
        final double[] STAR_VALUES = { 2, 3, 4 };

        // Scan for sequences stars
        // Start from max star count to prevent lower star counts from identifying first
        final int[] starCounts = new int[MAX_STARS];

        for (int starNumber = MAX_STARS; starNumber > 0; starNumber--) {
            starCounts[starNumber - 1] = 0;

            if (line.contains("*".repeat(starNumber) + " ".repeat(MAX_STARS - starNumber))) {
                starCounts[starNumber - 1]++;
                break;
            }
        }

        // Count dots in line.
        final double dotCount = (line.length() - line.replace(".  ", "").length()) / 3;
        double value = dotCount / 3;
        for (int i = 0; i < MAX_STARS; i++) {
            value += starCounts[i] * STAR_VALUES[i];
        }

        return value;
    }

    private static String[] joinArrays(final String[] first, final String[] second) {
        final List<String> both = new ArrayList<String>(first.length + second.length);
        Collections.addAll(both, first);
        Collections.addAll(both, second);
        return both.toArray(new String[0]);
    }
}