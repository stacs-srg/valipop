package uk.ac.standrews.cs.valipop.utils;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.StatsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class RCaller {

    public static Process generateAnalysisHTML(Path pathOfRunDir, int maxBirthingAge, String subTitle) throws StatsException {

        String pathToScript = "src/main/resources/valipop/analysis-r/geeglm/runPopulationAnalysis.R";
        String[] params = {System.getProperty("user.dir") + "/" + pathOfRunDir, String.valueOf(maxBirthingAge), subTitle};

        try {
            return runRScript(pathToScript, params);
        } catch (IOException e) {
            throw new StatsException(e.getMessage());
        }
    }

    public static double getV(Path pathOfTablesDir, int maxBirthingAge) throws StatsException, IOException {

        String pathOfScript = "src/main/resources/valipop/analysis-r/geeglm/dev-minima-search.R";
        String[] params = {pathOfTablesDir.toString(), String.valueOf(maxBirthingAge)};

        Process proc = runRScript(pathOfScript, params);
        String[] res = waitOnReturn(proc).split(" ");

        if (res.length != 2) {
            throw new StatsException("Too many values returned from RScript for given script");
        }

        return Double.parseDouble(res[1]);
    }

    public static double getObV(Path pathOfTablesDir, int maxBirthingAge) throws StatsException, IOException {

        String pathOfScript = "src/main/resources/valipop/analysis-r/geeglm/ob-minima-search.R";
        String[] params = {pathOfTablesDir.toString(), String.valueOf(maxBirthingAge)};

        Process proc = runRScript(pathOfScript, params);
        String[] res = waitOnReturn(proc).split(" ");

        if (res.length != 2) {
            throw new StatsException("Too many values returned from RScript for given script");
        }

        return Double.parseDouble(res[1]);
    }

    public static double getGeeglmV(String title, Path pathOfRunDir, int maxBirthingAge, LocalDateTime startTime) throws IOException, StatsException {

        while (true) {
            Process p = generateAnalysisHTML(pathOfRunDir, maxBirthingAge, title);
            waitOnReturn(p);

            String pathOfScript = "src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh";
            String[] params = {pathOfRunDir + "/analysis.html", pathOfRunDir + "/failtures.txt"};

            Process proc = runProcess("/bin/sh", pathOfScript, params);
            String[] res = waitOnReturn(proc).split(" ");

            if (res.length != 1) {
                throw new StatsException("Too many values returned from sh for given script");
            }

            // This checks to ensure that the parallelism bug in knitr hasn't affected this analysis run
            String checkScript = "src/main/resources/valipop/analysis-r/paper/code/re-runs/checker.sh";
            String[] checkParams = {pathOfRunDir + "/analysis.html", Config.formatTimeStamp(startTime)};
            Process checkProc = runProcess("/bin/sh", checkScript, checkParams);
            String checkRes = waitOnReturn(checkProc);

            if (checkRes.equals("CORRECT\n")) {
                return Double.parseDouble(res[0]);
            }
        }
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