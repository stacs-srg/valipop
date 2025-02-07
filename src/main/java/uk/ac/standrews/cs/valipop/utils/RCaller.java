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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * Invokes R code.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class RCaller {

    public static Process generateAnalysisHTML(Path projectPath, Path pathOfRunDir, int maxBirthingAge, String subTitle) throws StatsException {

        String pathToScript = projectPath.toAbsolutePath().toString() + "/src/main/resources/valipop/analysis-r/geeglm/runPopulationAnalysis.R";
        String[] params = {pathOfRunDir.toAbsolutePath().toString(), String.valueOf(maxBirthingAge), subTitle};
//        String[] params = {System.getProperty("user.dir") + "/" + pathOfRunDir, String.valueOf(maxBirthingAge), subTitle};

        try {
            return runRScript(pathToScript, params);
        } catch (IOException e) {
            throw new StatsException(e.getMessage());
        }
    }

    public static double getGeeglmV(String title, Path projectPath, Path pathOfRunDir, int maxBirthingAge, LocalDateTime startTime) throws IOException, StatsException {
        // Combine the R files into a single file 
        String[] scripts = new String[]{
            "valipop/analysis-r/geeglm/process-data-functions.R",
            "valipop/analysis-r/geeglm/id-funtions.R",
            "valipop/analysis-r/geeglm/geeglm-functions.R",
            "valipop/analysis-r/geeglm/analysis.R"
        };

        // The file the R is written to
        Path analysisPath = pathOfRunDir.resolve("analysis.R");
        File analysisFile = new File(analysisPath.toString());

        // This overwrites any existing file of the same name
        FileWriter analysisFileWriter = new FileWriter(analysisFile, false);
        analysisFileWriter.close();

        // The file the output of the analysis is written to
        Path outputPath = pathOfRunDir.resolve("analysis.out");
        File outputFile = new File(outputPath.toString());
        FileWriter outputFileWrtier = new FileWriter(outputFile, false);
        outputFile.createNewFile();

        for (String script : scripts) {
            try (
                // Retrieving the R files as streams in case they are in a jar
                InputStream stream = RCaller.class.getClassLoader().getResourceAsStream(script);
                OutputStream output = new FileOutputStream(analysisFile, true)
            ) {
                IOUtils.copy(stream, output);
            }
        }

        String[] params = {pathOfRunDir.toAbsolutePath().toString(), String.valueOf(maxBirthingAge)};

        System.out.println("Running command: Rscript " + analysisPath.toString() + " " + String.join(" ",params));
        Process p = runRScript(analysisPath.toString(), params);

        // Extracting stdout and stderr
        BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

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
        p.destroy();
        outputFileWrtier.close();

        System.out.println("Result: " + v);

        return v;
    }

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

    private static Process runRScript(String pathOfScript, String[] params) throws IOException {
        String[] commands = {"Rscript", pathOfScript};
        commands = joinArrays(commands, params);
        ProcessBuilder pb = new ProcessBuilder(commands);

        return pb.start();
    }

    private static String[] joinArrays(String[] first, String[] second) {
        List<String> both = new ArrayList<String>(first.length + second.length);
        Collections.addAll(both, first);
        Collections.addAll(both, second);
        return both.toArray(new String[0]);
    }
}