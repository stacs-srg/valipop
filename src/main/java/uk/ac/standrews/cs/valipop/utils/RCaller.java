package uk.ac.standrews.cs.valipop.utils;

import uk.ac.standrews.cs.valipop.implementations.StatsException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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

    public static double getV(Path projectPath, Path pathOfTablesDir, int maxBirthingAge) throws StatsException, IOException {

        String pathOfScript = projectPath.toAbsolutePath().toString() + "/src/main/resources/valipop/analysis-r/geeglm/dev-minima-search.R";
        String[] params = {pathOfTablesDir.toString(), String.valueOf(maxBirthingAge)};

        Process proc = runRScript(pathOfScript, params);
        String[] res = waitOnReturn(proc).split(" ");

        proc.destroy();

        if (res.length != 2) {
            throw new StatsException("Too many values returned from RScript for given script");
        }

        return Double.parseDouble(res[1]);
    }

    public static double getObV(Path projectPath, Path pathOfTablesDir, int maxBirthingAge) throws StatsException, IOException {

        String pathOfScript = projectPath.toAbsolutePath().toString() + "/src/main/resources/valipop/analysis-r/geeglm/ob-minima-search.R";
        String[] params = {pathOfTablesDir.toString(), String.valueOf(maxBirthingAge)};

        Process proc = runRScript(pathOfScript, params);
        String[] res = waitOnReturn(proc).split(" ");

        proc.destroy();

        if (res.length != 2) {
            throw new StatsException("Too many values returned from RScript for given script");
        }

        return Double.parseDouble(res[1]);
    }

    public static double getGeeglmV(String title, Path projectPath, Path pathOfRunDir, int maxBirthingAge, LocalDateTime startTime) throws IOException, StatsException {
        // Combine the R files into a single file 
        String[] scripts = new String[]{
            "valipop/analysis-r/geeglm/process-data-functions.R",
            "valipop/analysis-r/geeglm/id-funtions.R",
            "valipop/analysis-r/geeglm/geeglm-functions.R",
            "valipop/analysis-r/geeglm/analysis.R"
        };

        Path analysisPath = pathOfRunDir.resolve("analysis.R");

        File file = new File(analysisPath.toString());
        for (String script : scripts) {
            try (
                // Retrieving the R files as streams in case they are in a jar
                InputStream stream = RCaller.class.getClassLoader().getResourceAsStream(script);
                OutputStream output = new FileOutputStream(file, true)
            ) {
                IOUtils.copy(stream, output);
            }
        }

        String[] params = {pathOfRunDir.toAbsolutePath().toString(), String.valueOf(maxBirthingAge)};

        System.out.println("Running command: RScript " + analysisPath.toString() + " " + String.join(" ",params));
        Process p = runRScript(analysisPath.toString(), params);

        // Extracting stdout and stderr
        BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        // Filter relevant lines, calculate v per line and sum together
        int v = stdout
            .lines()
            .filter(RCaller::filterAnalysis)
            .map(RCaller::countV)
            .reduce(Integer::sum).orElse(0);

        // Print out any errors
        stderr.lines().forEach(System.out::println);

        // Clean up
        stdout.close();
        stderr.close();
        p.destroy();

        System.out.println("Result: " + v);

        return v;
    }

    private static boolean filterAnalysis(String line) {
        // Only STATS interactions are signficant; 
        return line.contains("STAT");
    }

    private static int countV(String line) {
        int MAX_STARS = 3;
        int[] STAR_VALUES = new int[]{ 2, 3, 4 };

        // Scan for sequences stars
        int[] starCounts = new int[MAX_STARS];
        for (int starNumber = MAX_STARS; starNumber > 0; starNumber--) {
            starCounts[starNumber - 1] = 0;

            if (line.indexOf(" " + "*".repeat(starNumber)) != -1) {
                starCounts[starNumber - 1]++;
                break;
            }
        }

        // Clever way to count dots in line
        int dotCount = (line.length() - line.replace(" .", "").length()) / 2;
        int value = dotCount;
        for (int i = 0; i < MAX_STARS; i++) {
            value += starCounts[i] * STAR_VALUES[i];
        }

        return value;
    }

    public static String waitOnReturn(Process process) throws IOException {


        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String result = "";
        String s;

        while ((s = stdInput.readLine()) != null) {
            result += s + "\n";
        }

        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        stdInput.close();
        stdError.close();

        return result;
    }

    private static Process runProcess(String processName, String pathOfScript, String[] params) throws IOException {

        String[] commands = {processName, pathOfScript};
        commands = joinArrays(commands, params);
        ProcessBuilder pb = new ProcessBuilder(commands);

        return pb.start();
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