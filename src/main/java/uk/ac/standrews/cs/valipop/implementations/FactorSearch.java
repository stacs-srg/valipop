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
package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.utils.AnalysisThread;
import uk.ac.standrews.cs.valipop.utils.ProcessArgs;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.IOException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FactorSearch {

    public static final int THREAD_LIMIT = 2;
    public static int threadCount = 0;

    public static void main(String[] args) throws IOException, InvalidInputFileException, StatsException, PreEmptiveOutOfMemoryWarning, InterruptedException {

        String[] pArgs = ProcessArgs.process(args, "FACTOR_SEARCH_PRECISION");
        if (!ProcessArgs.check(pArgs, "FACTOR_SEARCH_PRECISION")) {
            System.err.println("Incorrect arguments given");
            throw new Error("Incorrect arguments given");
        }

        String dataFiles = pArgs[0];
        int seedSize = Integer.valueOf(pArgs[1]);
        String runPurpose = pArgs[2];
        System.out.println(pArgs[3]);
        int numberOfRunsPerSim = Integer.valueOf(pArgs[3]);

        String rfsArg = pArgs[4];
        String prfsArg = pArgs[5];

        String resultsDir = pArgs[6];

        double[] precisions = toDoubleArray(pArgs[7]);

        double[] rfs = toDoubleArray(rfsArg);
        double[] prfs = toDoubleArray(prfsArg);

        runFactorSearch(seedSize, rfs, prfs, precisions, dataFiles, numberOfRunsPerSim, runPurpose, resultsDir);
    }

    private static double[] toDoubleArray(String rfsArg) {

        String[] split = rfsArg.split(",");
        double[] ret = new double[split.length];

        int c = 0;

        for(String s : split) {
            ret[c++] = Double.valueOf(s);
        }

        return ret;
    }

    static double[] bfs;
    static double[] dfs;

    static CompoundTimeUnit[] iws;
    static int[] minBirthSpacings;
    static int[] t0_pop_size;

    static RecordFormat output_record_format = RecordFormat.NONE;

    static CompoundTimeUnit simulation_time_step = new CompoundTimeUnit(1, TimeUnit.YEAR);
    static AdvanceableDate tS = new YearDate(1599);
    static AdvanceableDate t0 = new YearDate(1855);
    static AdvanceableDate tE = new YearDate(2015);
    static double set_up_br = 0.0233;
    static double set_up_dr = 0.0322;

    public static void runFactorSearch(int size0, double[] rfs, double[] prfs, double[] precisions, String dataFiles, int numberOfRunsPerSim, String runPurpose, String results_save_location) throws IOException, PreEmptiveOutOfMemoryWarning, InterruptedException {

        iws = new CompoundTimeUnit[]{
                new CompoundTimeUnit(10, TimeUnit.YEAR)
        };

        minBirthSpacings = new int[]{147};
        bfs = new double[]{0};
        dfs = new double[]{0};
        t0_pop_size = new int[]{size0};

        for(double precision : precisions) {
            CTtree.NODE_MIN_COUNT = precision;

            for (int size : t0_pop_size) {
                for (double rf : rfs) {
                    for (double prf : prfs) {
                        for (CompoundTimeUnit iw : iws) {
                            for (int minBirthSpacing : minBirthSpacings) {
                                for (double bf : bfs) {
                                    for (double df : dfs) {

                                        try {

                                            for (int n = 0; n < numberOfRunsPerSim; n++) {

                                                String startTime = FileUtils.getDateTime();
                                                OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, results_save_location);

                                                Config config = new Config(tS, t0, tE, size, set_up_br, set_up_dr,
                                                        simulation_time_step, dataFiles, results_save_location, runPurpose,
                                                        minBirthSpacing, minBirthSpacing, true, bf, df, rf, prf, iw, output_record_format, startTime, 0, false);

                                                OBDModel model = new OBDModel(startTime, config);
                                                try {
                                                    model.runSimulation();
                                                    model.analyseAndOutputPopulation(false);
                                                } catch (PreEmptiveOutOfMemoryWarning e) {
                                                    model.getSummaryRow().outputSummaryRowToFile();
                                                    throw e;
                                                }

                                                while (threadCount >= THREAD_LIMIT) {
                                                    Thread.sleep(10000);
                                                }

                                                new AnalysisThread(model, runPurpose).start();
                                            }

                                        } catch (IOException e) {
                                            String message = "Model failed due to Input/Output exception, check that this program has " +
                                                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                                                    "specified in config setup code : " + e.getMessage();
                                            throw new IOException(message, e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
