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
import uk.ac.standrews.cs.valipop.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.valipop.Config.CONTINGENCY_TABLES_DIR_NAME;
import static uk.ac.standrews.cs.valipop.Config.VALIDATION_ANALYSIS_DIR_NAME;
import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable.LABEL_SOURCE;
import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable.NUMERICAL_VARIABLES;
import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SourceType.TARGET;

/**
 * For extracting, invoking, and reading the results of the R analysis scripts.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 * @author Daniel Brathagen (db255@st-andrews.ac.uk)
 */
public class ContingencyTableValidator {

    private static final String ANALYSIS_SCRIPT_FILENAME = "validation-analysis.R";
    private static final String ANALYSIS_FILENAME = "validation-analysis-full.txt";
    private static final String ANALYSIS_RELEVANT_FILENAME = "validation-analysis-relevant.txt";
    private static final String ANALYSIS_SIGNIFICANT_FILENAME = "validation-analysis-significant.txt";
    private static final String ANALYSIS_SCORE_FILENAME = "validation-analysis-score.txt";

    private static final int NORMAL_EXIT_CODE = 0;
    private static final int R_PROCESS_TIMEOUT_IN_MINUTES = 10;

    // Relative to src/main/resources.
    private static final Path ANALYSIS_SCRIPT_SOURCE_DIRECTORY = Path.of("valipop/analysis-r/geeglm/");

    private final Path workingDirectoryPath;
    private final Path contingencyTablesDirectoryPath;
    private final Path analysisFile;
    private final Path analysisRelevantFile;
    private final Path analysisSignificantFile;
    private final Path analysisScoreFile;
    private final Path analysisScriptSource;
    private final Path analysisScriptTempCopy;

    private final int startYear;
    private final int endYear;

    public ContingencyTableValidator(final Config config) {

        workingDirectoryPath = config.getRunPath();
        contingencyTablesDirectoryPath = workingDirectoryPath.resolve(CONTINGENCY_TABLES_DIR_NAME);

        final Path validationAnalysisDirectoryPath = workingDirectoryPath.resolve(VALIDATION_ANALYSIS_DIR_NAME);

        analysisFile = validationAnalysisDirectoryPath.resolve(ANALYSIS_FILENAME);
        analysisRelevantFile = validationAnalysisDirectoryPath.resolve(ANALYSIS_RELEVANT_FILENAME);
        analysisSignificantFile = validationAnalysisDirectoryPath.resolve(ANALYSIS_SIGNIFICANT_FILENAME);
        analysisScoreFile = validationAnalysisDirectoryPath.resolve(ANALYSIS_SCORE_FILENAME);

        analysisScriptSource = ANALYSIS_SCRIPT_SOURCE_DIRECTORY.resolve(ANALYSIS_SCRIPT_FILENAME);
        analysisScriptTempCopy = validationAnalysisDirectoryPath.resolve(ANALYSIS_SCRIPT_FILENAME);

        startYear = config.getSimulationStart().getYear();
        endYear = config.getSimulationEnd().getYear();
    }

    @SuppressWarnings("StringConcatenationMissingWhitespace")
    public synchronized void validate() throws IOException {

        // Synchronized since method creates and deletes files in the parent directory.

        runAnalysis();

        int relevantInteractions = 0;
        int significantInteractions = 0;
        int starCount = 0;

        try (final FileWriter analysisRelevantFileWriter = new FileWriter(analysisRelevantFile.toString(), false)) {

            Files.readAllLines(analysisFile).stream().
                filter(ContingencyTableValidator::lineIsRelevant).
                forEach(line -> {
                    try {
                        analysisRelevantFileWriter.append(line).append("\n");
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }

        try (final FileWriter analysisSignificantFileWriter = new FileWriter(analysisSignificantFile.toString(), false)) {

            for (final String line : Files.readAllLines(analysisRelevantFile)) {

                if (line.contains(":" + LABEL_SOURCE + TARGET)) relevantInteractions++;

                if (line.isEmpty() || line.contains("-----") || line.contains("Pr(>|W|)"))
                    analysisSignificantFileWriter.append(line).append("\n");

                if (line.contains("*") && line.contains(":" + LABEL_SOURCE + TARGET)) {

                    significantInteractions++;
                    starCount += countStars(line);
                    analysisSignificantFileWriter.append(line).append("\n");
                }
            }
        }

        final String averageFormatted = String.format("%.2f", (double) starCount / relevantInteractions);

        try (final FileWriter analysisScoreFileWriter = new FileWriter(analysisScoreFile.toString(), false)) {

            analysisScoreFileWriter.append("overall_score = ").append(String.valueOf(starCount)).append("\n");
            analysisScoreFileWriter.append("relevant_interactions = ").append(String.valueOf(relevantInteractions)).append("\n");
            analysisScoreFileWriter.append("significant_interactions = ").append(String.valueOf(significantInteractions)).append("\n");
            analysisScoreFileWriter.append("score_per_interaction = ").append(averageFormatted).append("\n");
        }
    }

    private void runAnalysis() throws IOException {

        makeTempCopyOfRScript();

        final Process process = runRScript();
        outputAnalysisResults(process, analysisFile);

        try {
            checkExitStatus(process);
        }
        finally {
            Files.delete(analysisScriptTempCopy);
        }
    }

    public int getValidationScore() throws IOException {

        try (final InputStreamReader reader = new InputStreamReader(Files.newInputStream(workingDirectoryPath.resolve(VALIDATION_ANALYSIS_DIR_NAME).resolve(ANALYSIS_SCORE_FILENAME)), StandardCharsets.UTF_8)) {

            final Properties properties = new Properties();
            properties.load(reader);
            return Integer.parseInt(properties.getProperty("overall_score"));
        }
    }

    private static boolean lineIsRelevant(final String line) {

        return line.isEmpty() || line.contains("-----") || line.contains("Pr(>|W|)") || lineContainsRelevantInteraction(line);
    }

    @SuppressWarnings("StringConcatenationMissingWhitespace")
    private static boolean lineContainsRelevantInteraction(final String line) {

        final boolean line_contains_numerical_variable = Arrays.stream(line.split(" ")[0].     // Potential interaction.
            split(":", -1)).                                                              // Variables within interaction.
            anyMatch(NUMERICAL_VARIABLES::contains);                                                 // Check for any numerical variables.

        return line.contains(LABEL_SOURCE + TARGET) && line_contains_numerical_variable;             // Check for interaction with TARGET variable.
    }

    private static int countStars(final String line) {
        return line.length() - line.replace("*", "").length();
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
     * Makes a temporary copy of the R analysis script in the local directory. Needed in the case of running from
     * a jar, and harmless otherwise.
     */
    private void makeTempCopyOfRScript() throws IOException {

        try (
            // Retrieve the R file as a stream in case it's in a jar.
            final InputStream stream = ContingencyTableValidator.class.getClassLoader().getResourceAsStream(analysisScriptSource.toString());
            final OutputStream output = Files.newOutputStream(analysisScriptTempCopy)
        ) {
            IOUtils.copy(stream, output);
        }
    }

    /**
     * Executes the R analysis script and returns the running process.
     *
     * @return the executing process
     */
    private Process runRScript() throws IOException {

        final String[] command = { "Rscript", analysisScriptTempCopy.toString(), contingencyTablesDirectoryPath.toAbsolutePath().toString(), String.valueOf(startYear), String.valueOf(endYear)};

        final ProcessBuilder builder = new ProcessBuilder(command);
        return builder.start();
    }

    /**
     * Outputs the standard output and error of the R analysis to {@code outputPath} and returns the calculated v value.
     * 
     * @param process the executing R analysis process
     * @param outputPath the path of the process output and error streams
     */
    private static void outputAnalysisResults(final Process process, final Path outputPath) throws IOException {

        try (
            final BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            final FileWriter outputFileWriter = new FileWriter(outputPath.toString(), false);
        ) {
            stdout.lines().
                forEach(line -> {
                    try {
                        outputFileWriter.append(line).append("\n");

                    } catch (final IOException e) {
                        System.err.println("Unable to write results of analysis to file " + outputPath);
                    }
                });

            // Print out any errors
            stderr.lines().forEach(System.err::println);
        }
    }
}