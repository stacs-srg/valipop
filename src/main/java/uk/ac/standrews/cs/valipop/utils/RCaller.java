package uk.ac.standrews.cs.valipop.utils;

import uk.ac.standrews.cs.valipop.implementations.StatsException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * For extracting, invoking, and reading the results of the R analysis scripts.
 * 
 * @author Daniel Brathagen (db255@st-andrews.ac.uk)
 */
public class RCaller {

    // Constants

    private static Path R_SCRIPT_LOCATION = Path.of("analysis.R");
    private static Path R_SCRIPT_OUTPUT_LOCATION = Path.of("analysis.out");

    private static String[] R_SCRIPT_PATHS = new String[]{
        "valipop/analysis-r/geeglm/process-data-functions.R",
        "valipop/analysis-r/geeglm/id-funtions.R",
        "valipop/analysis-r/geeglm/geeglm-functions.R",
        "valipop/analysis-r/geeglm/analysis.R"
    };

    // Public Methods

    /**
     * Extracts and concats the R analysis scripts to a local file.
     * 
     * @param rScriptPath the full path to extract the scripts to
     * 
     * @return the parameter {@code rScriptPath}
     */
    public static Path extractRScript(Path rScriptPath) throws IOException {
        // The file the R is written to
        File rScriptFile = new File(rScriptPath.toString());

        // This overwrites any existing file of the same name
        FileWriter rScriptFileWriter = new FileWriter(rScriptFile, false);
        rScriptFileWriter.close();

        for (String script : R_SCRIPT_PATHS) {
            try (
                // Retrieving the R files as streams in case they are in a jar
                InputStream stream = RCaller.class.getClassLoader().getResourceAsStream(script);
                OutputStream output = new FileOutputStream(rScriptFile, true)
            ) {
                IOUtils.copy(stream, output);
            }
        }

        return rScriptPath;
    }

    /**
     * Executes the R analysis script and returns the running process. The process must be destroyed separately.
     * 
     * @param runDirPath the path of the current run directory
     * @param rScriptPath the path of the R analysis script
     * @param maxBirthingAge the maximum birthing age of the population model
     * 
     * @return the executing process
     */
    public static Process runRScript(Path runDirPath, Path rScriptPath, int maxBirthingAge) throws IOException {
        String[] params = {runDirPath.toAbsolutePath().toString(), String.valueOf(maxBirthingAge)};
        String[] commands = joinArrays(new String[]{ "Rscript", rScriptPath.toString()}, params);

        System.out.println("Running command: Rscript " + String.join(" ", commands));
        ProcessBuilder pb = new ProcessBuilder(commands);
        return pb.start();
    }

    /**
     * Outputs the standard otuput and error of the R analysis to {@code outputPath} and returns the calculated v value.
     * 
     * @param process the executing R analysis process
     * @param outputPath the path of the process output and error streams
     */
    public static double getRScriptResult(Process process, Path outputPath) throws IOException {
        // Extracting stdout and stderr
        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // The file the output of the R script is written to
        File outputFile = new File(outputPath.toString());
        FileWriter outputFileWrtier = new FileWriter(outputFile, false);
        outputFile.createNewFile();

        // Filter relevant lines, calculate v per line and sum together
        int v = stdout
            .lines()
            // Writing lines to file
            .map((l) -> {
                try {
                    outputFileWrtier.write(l);
                    outputFileWrtier.append("\n");
                } catch (IOException e) {
                    System.err.println("Unable to write results of analysis to file " + outputPath.toString());
                }

                return l;
            })
            .filter(RCaller::filterAnalysis)
            .map(RCaller::countV)
            .reduce(Double::sum)
            .map((res) -> (int) Math.floor(res))
            .orElse(0);

        // Print out any errors
        stderr.lines().forEach(System.out::println);

        // Clean up
        stdout.close();
        stderr.close();
        outputFileWrtier.close();

        System.out.println("Result: " + v);

        return v;
    }

    /**
     * Runs the R analysis on the population model and returns the analysis result
     * 
     * @param runDirPath the path of the run directory 
     * @param maxBirthingAge the maximum birthing age of the population model
     */
    public static double getGeeglmV(Path runDirPath, int maxBirthingAge) throws IOException, StatsException {
        Path rScriptPath = extractRScript(runDirPath.resolve(R_SCRIPT_LOCATION));
        Process process = runRScript(runDirPath, rScriptPath, maxBirthingAge);
        double v = getRScriptResult(process, runDirPath.resolve(R_SCRIPT_OUTPUT_LOCATION));

        process.destroy();

        return v;
    }

    // Private methods

    private static boolean filterAnalysis(String line) {
        // Only STATS interactions are signficant; 
        return line.contains("STAT");
    }

    private static double countV(String line) {
        int MAX_STARS = 3;
        double[] STAR_VALUES = new double[]{ 2, 3, 4 };

        // Scan for sequences stars
        // Start from max star count to prevent lower star counts from identifying first
        int[] starCounts = new int[MAX_STARS];
        for (int starNumber = MAX_STARS; starNumber > 0; starNumber--) {
            starCounts[starNumber - 1] = 0;

            if (line.indexOf("*".repeat(starNumber) + " ".repeat(MAX_STARS - starNumber)) != -1) {
                starCounts[starNumber - 1]++;
                break;
            }
        }

        // Clever way to count dots in line
        double dotCount = (line.length() - line.replace(".  ", "").length()) / 3;
        double value = dotCount / 3;
        for (int i = 0; i < MAX_STARS; i++) {
            value += starCounts[i] * STAR_VALUES[i];
        }

        return value;
    }

    private static String[] joinArrays(String[] first, String[] second) {
        List<String> both = new ArrayList<String>(first.length + second.length);
        Collections.addAll(both, first);
        Collections.addAll(both, second);
        return both.toArray(new String[0]);
    }
}