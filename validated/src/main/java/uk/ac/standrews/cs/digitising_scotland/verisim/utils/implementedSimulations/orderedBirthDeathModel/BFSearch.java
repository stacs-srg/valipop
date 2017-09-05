package uk.ac.standrews.cs.digitising_scotland.verisim.utils.implementedSimulations.orderedBirthDeathModel;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProcessArgs;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BFSearch {

    public static void main(String[] args) {
        runBFSearch(args);
    }

    public static void runBFSearch(String[] args) {

        String[] pArgs = ProcessArgs.process(args, "BF_SEARCH");
        if(!ProcessArgs.check(pArgs, "BF_SEARCH")) {
            System.err.println("Incorrect arguments given");
            System.exit(1);
        }

        String pathToConfigFile = pArgs[0];
        String resultsPath = pArgs[1];
        String runPurpose = pArgs[2];

        int numberOfRuns = Integer.parseInt(pArgs[3]);
        double bfStart = Double.parseDouble(pArgs[4]);
        double bfStep = Double.parseDouble(pArgs[5]);
        double bfEnd = Double.parseDouble(pArgs[6]);

        executeNFullPopulationRunsAccrossBirthFactor(pathToConfigFile, resultsPath, runPurpose, numberOfRuns, bfStart, bfStep, bfEnd);

    }

    private static void executeNFullPopulationRunsAccrossBirthFactor(String pathToConfigFile, String resultsPath, String runPurpose,
                                                                     int numberOfRuns, double bfStart, double bfStep, double bfEnd) {

        for(double bf = bfStart; bf <= bfEnd; bf += bfStep) {

            OBDModel.executeNFullPopulationRuns(pathToConfigFile, resultsPath, runPurpose, numberOfRuns, bf);

        }

    }

}
