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

import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.implementations.minimaSearch.Control;
import uk.ac.standrews.cs.valipop.implementations.minimaSearch.MinimaSearch;
import uk.ac.standrews.cs.valipop.implementations.minimaSearch.Minimise;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.utils.ProcessArgs;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.RCaller;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.IOException;

import static uk.ac.standrews.cs.valipop.implementations.minimaSearch.Minimise.GEEGLM;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FactorSearch {

    public static void main(String[] args) throws IOException, InvalidInputFileException, StatsException, PreEmptiveOutOfMemoryWarning {

        String[] pArgs = ProcessArgs.process(args, "N-RUNS");
        if (!ProcessArgs.check(pArgs, "N-RUNS")) {
            System.err.println("Incorrect arguments given");
            throw new Error("Incorrect arguments given");
        }

        String dataFiles = pArgs[0];
        int seedSize = Integer.valueOf(pArgs[1]);
        String runPurpose = pArgs[2];
        int numberOfRunsPerSim = Integer.valueOf(pArgs[3]);

        runFactorSearch(seedSize, dataFiles, numberOfRunsPerSim, runPurpose);

    }

    static double[] rfs;
    static double[] prfs;
    static double[] maxInfids;
    static double[] bfs;
    static double[] dfs;
    static CompoundTimeUnit[] iws;
    static int[] minBirthSpacings;
    static int[] t0_pop_size;

    // "src/main/resources/scotland_test_population"
    static String var_data_files = "src/main/resources/proxy-scotland-population-JA";
    static String results_save_location = "src/main/resources/valipop/results/";
    static RecordFormat output_record_format = RecordFormat.NONE;

    static CompoundTimeUnit simulation_time_step = new CompoundTimeUnit(1, TimeUnit.YEAR);
    static AdvancableDate tS = new YearDate(1599);
    static AdvancableDate t0 = new YearDate(1855);
    static AdvancableDate tE = new YearDate(2015);
    static double set_up_br = 0.0233;
    static double set_up_dr = 0.0322;

    public static void runFactorSearch(int size0, String dataFiles, int numberOfRunsPerSim, String runPurpose) throws IOException, InvalidInputFileException, StatsException, PreEmptiveOutOfMemoryWarning {

        rfs = new double[]{1.0};
        prfs = new double[]{0.0, 0.3, 0.6, 1.0};
        iws = new CompoundTimeUnit[]{
                new CompoundTimeUnit(10, TimeUnit.YEAR)
        };
        minBirthSpacings = new int[]{147};
        bfs = new double[]{0};
        dfs = new double[]{0};
        t0_pop_size = new int[]{31250};


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
                                                    minBirthSpacing, minBirthSpacing, true, bf, df, rf, prf, iw, output_record_format, startTime);


                                            OBDModel model = new OBDModel(startTime, config);
                                            try {
                                                model.runSimulation();
                                                model.analyseAndOutputPopulation(false);
                                            } catch (PreEmptiveOutOfMemoryWarning e) {
                                                model.getSummaryRow().outputSummaryRowToFile();
                                                throw e;
                                            }

                                            ProgramTimer statsTimer = new ProgramTimer();

                                            Integer maxBirthingAge = model.getDesiredPopulationStatistics()
                                                    .getOrderedBirthRates(new YearDate(0)).getLargestLabel().getValue();
                                            double v = MinimaSearch.getV(GEEGLM, maxBirthingAge, runPurpose, Control.BF);

                                            model.getSummaryRow().setV(v);
                                            model.getSummaryRow().setStatsRunTime(statsTimer.getRunTimeSeconds());

                                            model.getSummaryRow().outputSummaryRowToFile();

                                        }

                                    } catch (IOException e) {
                                        String message = "Model failed due to Input/Output exception, check that this program has " +
                                                "permission to read or write on disk. Also, check supporting input files are present at location " +
                                                "specified in config setup code : " + e.getMessage();
                                        throw new IOException(message, e);
                                    } catch (InvalidInputFileException | InconsistentWeightException e) {
                                        String message = "Model failed due to an invalid formatting/content of input file, see message: " + e.getMessage();
                                        throw new InvalidInputFileException(message, e);
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
