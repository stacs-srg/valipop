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
import java.util.*;
import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.valipop.Config.CONTINGENCY_TABLES_DIR_NAME;
import static uk.ac.standrews.cs.valipop.Config.VALIDATION_ANALYSIS_DIR_NAME;
import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable.*;
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
    private static final String ANALYSIS_SCORE_FILENAME = "validation-analysis-score.txt";

    private static final int NORMAL_EXIT_CODE = 0;
    private static final int R_PROCESS_TIMEOUT_IN_MINUTES = 10;

    // Relative to src/main/resources.
    private static final Path ANALYSIS_SCRIPT_SOURCE_DIRECTORY = Path.of("R/geeglm/");

    private final Path workingDirectoryPath;
    private final Path contingencyTablesDirectoryPath;
    private final Path analysisFile;
    private final Path analysisRelevantFile;
    private final Path analysisScoreFile;
    private final Path analysisScriptSource;
    private final Path analysisScriptTempCopy;

    private final int startYear;
    private final int endYear;

    @SuppressWarnings("BooleanVariableAlwaysNegated")
    private final boolean suppressSolelyCategoricalCorrelations;

    public ContingencyTableValidator(final Config config) {

        workingDirectoryPath = config.getRunPath();
        contingencyTablesDirectoryPath = workingDirectoryPath.resolve(CONTINGENCY_TABLES_DIR_NAME);

        final Path validationAnalysisDirectoryPath = workingDirectoryPath.resolve(VALIDATION_ANALYSIS_DIR_NAME);

        analysisFile = validationAnalysisDirectoryPath.resolve(ANALYSIS_FILENAME);
        analysisRelevantFile = validationAnalysisDirectoryPath.resolve(ANALYSIS_RELEVANT_FILENAME);
        analysisScoreFile = validationAnalysisDirectoryPath.resolve(ANALYSIS_SCORE_FILENAME);

        analysisScriptSource = ANALYSIS_SCRIPT_SOURCE_DIRECTORY.resolve(ANALYSIS_SCRIPT_FILENAME);
        analysisScriptTempCopy = validationAnalysisDirectoryPath.resolve(ANALYSIS_SCRIPT_FILENAME);

        startYear = config.getSimulationStart().getYear();
        endYear = config.getSimulationEnd().getYear();

        suppressSolelyCategoricalCorrelations = config.shouldSuppressSolelyCategoricalCorrelations();
    }

    public synchronized void validate() throws IOException {

        // Synchronized since method creates and deletes files in the parent directory.

        runAnalysis();

        int relevantInteractions = 0;
        int significantInteractions = 0;
        int starCount = 0;

        try (final FileWriter analysisRelevantFileWriter = new FileWriter(analysisRelevantFile.toString(), false)) {

            final List<String> linesInAnalysisFile = Files.readAllLines(analysisFile);
            List<String> linesForParticularAnalysis;

            int line_no = 0;
            while (!(linesInAnalysisFile.get(line_no).contains("-----"))) line_no++;

            while (line_no < linesInAnalysisFile.size()) {

                analysisRelevantFileWriter.append("\n").append(linesInAnalysisFile.get(line_no)).append("\n").append("\n");

                linesForParticularAnalysis = new ArrayList<>();

                while (!(linesInAnalysisFile.get(line_no).contains("(Intercept)"))) line_no++;
                line_no++;

                while (!(linesInAnalysisFile.get(line_no).contains("---") || linesInAnalysisFile.get(line_no).isEmpty())) {
                    linesForParticularAnalysis.add(linesInAnalysisFile.get(line_no));
                    line_no++;
                }

                final Map<String, Integer> maxStarCounts = new TreeMap<>(ContingencyTableValidator::compareAnalysisKeys);

                int max_key_length = 0;
                for (final String line1 : linesForParticularAnalysis) {
                    if (lineIsRelevant(line1)) {
                        relevantInteractions++;

                        final String s = line1.split(" ")[0];
                        String key = s;
                        final List<String> variables = Arrays.stream(s.split(":", -1)).filter(variable -> !variable.equals("SourceTARGET")).toList();

                        for (final String variable : variables)
                            if (prefixedByCategoricalVariable(variable))
                                key = s.replace(variable, getCategoricalVariablePrefix(variable));

                        final int stars = countStars(line1);
                        if (maxStarCounts.containsKey(key)) {
                            maxStarCounts.put(key, Math.max(maxStarCounts.get(key), stars));
                        } else {
                            maxStarCounts.put(key, stars);
                        }

                        if (stars > 0) significantInteractions++;
                        starCount += stars;

                        if (key.length() > max_key_length) max_key_length = key.length();
                    }
                }

                for (final String key : maxStarCounts.keySet()) {
                    analysisRelevantFileWriter.append(key);
                    for (int i = 0; i <= max_key_length - key.length(); i++) analysisRelevantFileWriter.append(" ");
                    for (int i = 0; i < maxStarCounts.get(key); i++) analysisRelevantFileWriter.append("*");
                    analysisRelevantFileWriter.append("\n");
                }

                while (line_no < linesInAnalysisFile.size() && !(linesInAnalysisFile.get(line_no).contains("-----")))
                    line_no++;
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

    private static int compareAnalysisKeys(final String s1, final String s2) {

        final int number_of_variables1 = s1.split(":", -1).length;
        final int number_of_variables2 = s2.split(":", -1).length;

        if (number_of_variables1 == number_of_variables2) return s1.compareTo(s2);
        else return Integer.compare(number_of_variables1, number_of_variables2);
    }

    private static boolean prefixedByCategoricalVariable(final String variable) {

        return getCategoricalVariablePrefix(variable) != null;
    }

    private static String getCategoricalVariablePrefix(final String variable) {

        for (final String categoricalVariable : CATEGORICAL_VARIABLES)
            if (variable.startsWith(categoricalVariable)) return categoricalVariable;

        return null;
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

    @SuppressWarnings("StringConcatenationMissingWhitespace")
    private boolean lineIsRelevant(final String line) {

        // Check for interaction with TARGET variable.
        if (!line.contains(LABEL_SOURCE + TARGET)) return false;

        if (!suppressSolelyCategoricalCorrelations) return true;

        // Check whether line contains any numerical variables.
        return Arrays.stream(line.split(" ")[0].
            split(":", -1)).
            anyMatch(NUMERICAL_VARIABLES::contains);
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
     * Outputs the standard output and error of the R analysis to {@code outputPath}.
     * 
     * @param process the executing R analysis process
     * @param outputPath the path of the process output
     */
    private static void outputAnalysisResults(final Process process, final Path outputPath) throws IOException {

        try (
            final BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            final FileWriter outputFileWriter = new FileWriter(outputPath.toString(), false)) {

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