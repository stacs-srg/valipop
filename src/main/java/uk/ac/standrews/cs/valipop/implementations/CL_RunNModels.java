package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.utils.ProcessArgs;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;

import java.io.IOException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CL_RunNModels {

    public static void main(String[] args) {

        CL_RunNModels.runNModels(args);

    }

    public static void runNModels(String[] args) {
        // Expects 4 args: path to config file, results path, run purpose

        String[] pArgs = ProcessArgs.process(args, "N-RUNS");
        if(!ProcessArgs.check(pArgs, "N-RUNS")) {
            System.err.println("Incorrect arguments given");
            throw new Error("Incorrect arguments given");
        }

        String pathToConfigFile = pArgs[0];
        String resultsPath = pArgs[1];
        String runPurpose = pArgs[2];

        int nRuns;
        try {
            nRuns = Integer.parseInt(pArgs[3]);
        } catch (NumberFormatException e) {
            System.err.println("Incorrect arguments given - arg 4 should be an integer");
            throw new Error("Incorrect arguments given - arg 4 should be an integer");
        }

        try {

            CTtree.reuseExpectedValues(true);

            for(int n = 0 ; n < nRuns; n++) {
                CL_OBDModel.runOBDModel(pathToConfigFile, resultsPath, runPurpose);
            }

        } catch (InvalidInputFileException | IOException | Error | PreEmptiveOutOfMemoryWarning e) {
            System.err.println(e.getMessage());
        }

    }


}
