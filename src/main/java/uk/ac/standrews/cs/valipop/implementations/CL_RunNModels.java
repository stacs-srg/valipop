package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.valipop.utils.ProcessArgs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CL_RunNModels {

    public static void main(String[] args) throws IOException, StatsException {

        CL_RunNModels.runNModels(args);
    }

    public static void runNModels(String[] args) throws IOException, StatsException {
        // Expects 4 args: path to config file, results path, run purpose, number of runs

        String[] pArgs = ProcessArgs.process(args, "N-RUNS");
        if (!ProcessArgs.check(pArgs, "N-RUNS")) {
            throw new Error("Incorrect arguments given");
        }

        Path pathToConfigFile = Paths.get(pArgs[0]);
        String resultsPath = pArgs[1];
        String runPurpose = pArgs[2];

        int nRuns = Integer.parseInt(pArgs[3]);

        for (int n = 0; n < nRuns; n++) {

            CL_OBDModel.runOBDModel(pathToConfigFile);
            System.gc();
        }
    }
}
