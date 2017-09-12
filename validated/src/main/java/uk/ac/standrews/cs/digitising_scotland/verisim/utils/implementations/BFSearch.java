/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.implementations;

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
