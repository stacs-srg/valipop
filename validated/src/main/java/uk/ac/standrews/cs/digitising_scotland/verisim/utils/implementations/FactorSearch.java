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

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.RecordFormat;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FactorSearch {

    public static void main(String[] args) {
        MemoryUsageAnalysis.setCheckMemory(true);
        runBFSearch();
    }


    static double[] rfs;
    static double[] maxInfids;
    static double[] bfs;
    static double[] dfs;
    static CompoundTimeUnit[] iws;
    static int[] minBirthSpacings;
    static int[] t0_pop_size;

    String var_data_files = "src/main/resources/scotland_test_population";
    String results_save_location = "src/main/resources/results/";
    RecordFormat output_record_format = RecordFormat.NONE;

    CompoundTimeUnit simulation_time_step = new CompoundTimeUnit(1, TimeUnit.YEAR);
    Date tS = new YearDate(1599);
    Date t0 = new YearDate(1855);
    Date tE = new YearDate(2015);
    double set_up_br = 0.0133;
    double set_up_dr = 0.0122;

    int numberOfRunsPerSim = 1;
    String runPurpose = "factor-exploration";

    public static void runBFSearch() {

        rfs = new double[]{0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        iws = new CompoundTimeUnit[]{
                new CompoundTimeUnit(1, TimeUnit.YEAR),
                new CompoundTimeUnit(10, TimeUnit.YEAR),
                new CompoundTimeUnit(20, TimeUnit.YEAR),
                new CompoundTimeUnit(50, TimeUnit.YEAR),
                new CompoundTimeUnit(100, TimeUnit.YEAR),
                new CompoundTimeUnit(500, TimeUnit.YEAR),
        };
        minBirthSpacings = new int[]{1, 147, 252, 365, 730};
        maxInfids = new double[]{0, 0.1, 0.2, 1};
        bfs = new double[]{0};
        dfs = new double[]{0};




//        executeNFullPopulationRunsAccrossBirthFactor(pathToConfigFile, resultsPath, runPurpose, numberOfRuns, bfStart, bfStep, bfEnd);

    }

    private static void executeNFullPopulationRunsAccrossBirthFactor(String pathToConfigFile, String resultsPath, String runPurpose,
                                                                     int numberOfRuns, double bfStart, double bfStep, double bfEnd) {

        for(double bf = bfStart; bf <= bfEnd; bf += bfStep) {

//            Config config = new Config(pathToConfigFile, runPurpose, )

//            OBDModel.executeNFullPopulationRuns(pathToConfigFile, resultsPath, runPurpose, numberOfRuns, bf);

        }

    }

}
