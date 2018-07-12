package uk.ac.standrews.cs.valipop.utils;

import uk.ac.standrews.cs.valipop.implementations.StatsException;

import javax.sql.rowset.spi.SyncResolver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class RCaller {

    public static Process generateAnalysisHTML(String pathOfRunDir, int maxBirthingAge, String subTitle) throws StatsException {

        String pathToScript = "src/main/resources/valipop/analysis-r/geeglm/runPopulationAnalysis.R";
        String[] params = {pathOfRunDir, String.valueOf(maxBirthingAge), subTitle};

        try {
            return runRScript(pathToScript, params);
        } catch (IOException e) {
            throw new StatsException(e.getMessage());
        }

    }

    public static double getV(String pathOfTablesDir, int maxBirthingAge) throws StatsException, IOException {

        String pathOfScript = "src/main/resources/valipop/analysis-r/geeglm/dev-minima-search.R";
        String[] params = {pathOfTablesDir, String.valueOf(maxBirthingAge)};

        Process proc = runRScript(pathOfScript, params);
        String[] res = waitOnReturn(proc).split(" ");

        if(res.length != 2) {
            throw new StatsException("Too many values returned from RScript for given script");
        }

        return Double.parseDouble(res[1]);

    }

    public static double getObV(String pathOfTablesDir, int maxBirthingAge) throws StatsException, IOException {

        String pathOfScript = "src/main/resources/valipop/analysis-r/geeglm/ob-minima-search.R";
        String[] params = {pathOfTablesDir, String.valueOf(maxBirthingAge)};

        Process proc = runRScript(pathOfScript, params);
        String[] res = waitOnReturn(proc).split(" ");

        if(res.length != 2) {
            throw new StatsException("Too many values returned from RScript for given script");
        }

        return Double.parseDouble(res[1]);

    }

    public static double getGeeglmV(String title, String pathOfRunDir, String pathOfTablesDir, int maxBirthingAge) throws IOException, StatsException {

        Process p = generateAnalysisHTML(pathOfRunDir, maxBirthingAge, title);
        waitOnReturn(p);

        String pathOfScript = "src/main/resources/valipop/analysis-r/geeglm/geeglm-minima-search.sh";
        String[] params = {pathOfRunDir + "/analysis.html", String.valueOf(maxBirthingAge)};

        Process proc = runProcess("/bin/sh", pathOfScript, params);
        String[] res = waitOnReturn(proc).split(" ");

        if(res.length != 1) {
            throw new StatsException("Too many values returned from sh for given script");
        }

        return Double.parseDouble(res[0]);

    }

    public static String waitOnReturn(Process process) throws IOException {

        String result = "";

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process.getErrorStream()));

        String s = null;

        while((s = stdInput.readLine())!=null) {
              result += s + "\n";
        }

        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        return result;

    }

    public static Process runProcess(String processName, String pathOfScript, String[] params) throws IOException {
        String[] commands = {processName, pathOfScript};
        commands = joinArrays(commands, params);
        ProcessBuilder pb = new ProcessBuilder(commands);

        return pb.start();
    }

    public static Process runRScript(String pathOfScript, String[] params) throws IOException {

        String[] commands = {"Rscript", pathOfScript};
        commands = joinArrays(commands, params);
        ProcessBuilder pb = new ProcessBuilder(commands);

        return pb.start();

    }

    private static String[] joinArrays(String[] first, String[] second) {
        List<String> both = new ArrayList<String>(first.length + second.length);
        Collections.addAll(both, first);
        Collections.addAll(both, second);
        return both.toArray(new String[both.size()]);
    }


}
