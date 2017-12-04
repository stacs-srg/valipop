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
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.IOException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FactorSearch {

    public static void main(String[] args) throws IOException, InvalidInputFileException, PreEmptiveOutOfMemoryWarning {

//        runFactorSearch();

        runFactorSearch(100000, "src/main/resources/proxy-scotland-population-JA");

//        try {
//            switch(args[0]) {
//                case "B":
//                    runPurpose = "geeglm-bf-ja";
//                    runFactorSearch(250000, "src/main/resources/proxy-scotland-population-JA");
//                    break;
//                case "A":
//
//                    runFactorSearch(100000, "src/main/resources/scotland_test_population");
//                    break;
//            }
//                case "AC":
//                    runPurpose = "geeglm-scot";
//                    runFactorSearch(2000000, "src/main/resources/scotland_test_population");
//                    break;
//                case "AD":
//                    runPurpose = "geeglm-scot";
//                    runFactorSearch(4000000, "src/main/resources/scotland_test_population");
//                    break;
//                case "BA":
//                    runPurpose = "geeglm-ja";
//                    runFactorSearch(500000, "src/main/resources/proxy-scotland-population-JA");
//                    break;
//                case "BB":
//                    runPurpose = "geeglm-ja";
//                    runFactorSearch(1000000, "src/main/resources/proxy-scotland-population-JA");
//                    break;
//                case "BC":
//                    runPurpose = "geeglm-ja";
//                    runFactorSearch(2000000, "src/main/resources/proxy-scotland-population-JA");
//                    break;
//                case "BD":
//                    runPurpose = "geeglm-ja";
//                    runFactorSearch(4000000, "src/main/resources/proxy-scotland-population-JA");
//                    break;
//                default:
//                    break;
//            }
//
//        } catch (IOException | InvalidInputFileException e) {
//            e.printStackTrace();
//            System.err.println(e.getMessage());
//        }
    }

    static double[] rfs;
    static double[] maxInfids;
    static double[] bfs;
    static double[] dfs;
    static CompoundTimeUnit[] iws;
    static int[] minBirthSpacings;
    static int[] t0_pop_size;

    // "src/main/resources/scotland_test_population"
    static String var_data_files = "src/main/resources/proxy-scotland-population-JA";
    static String results_save_location = "src/main/resources/results/";
    static RecordFormat output_record_format = RecordFormat.NONE;

    static CompoundTimeUnit simulation_time_step = new CompoundTimeUnit(1, TimeUnit.YEAR);
    static AdvancableDate tS = new YearDate(1599);
    static AdvancableDate t0 = new YearDate(1855);
    static AdvancableDate tE = new YearDate(2015);
    static double set_up_br = 0.0133;
    static double set_up_dr = 0.0122;

    static int numberOfRunsPerSim = 1;
    static String runPurpose = "ja-year-set";

    public static void runFactorSearch(int size, String dataFiles) throws IOException, InvalidInputFileException, PreEmptiveOutOfMemoryWarning {

        rfs = new double[]{0.5};
        iws = new CompoundTimeUnit[]{
                new CompoundTimeUnit(40, TimeUnit.YEAR)
        };
        minBirthSpacings = new int[]{147};
        maxInfids = new double[]{0.2};
        bfs = new double[]{0};
        dfs = new double[]{0};
//        t0_pop_size = new int[]{1000000, 500000, 250000};


//        for(int size : t0_pop_size) {
            for (double rf : rfs) {
                for (CompoundTimeUnit iw : iws) {
                    for (int minBirthSpacing : minBirthSpacings) {
                        for (double maxInfid : maxInfids) {
                            for (double bf : bfs) {
                                for (double df : dfs) {

                                    try {

                                        for(int n = 0; n < numberOfRunsPerSim; n++) {

                                            if(n == 1) {
                                                CTtree.reuseExpectedValues(true);
                                            }

                                            String startTime = FileUtils.getDateTime();
                                            OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, results_save_location);

                                            Config config = new Config(tS, t0, tE, size, set_up_br, set_up_dr,
                                                    simulation_time_step, dataFiles, results_save_location, runPurpose,
                                                    minBirthSpacing, maxInfid, bf, df, rf, iw, output_record_format, startTime);


                                            OBDModel model = new OBDModel(startTime, config);
                                            model.runSimulation();
                                            model.analyseAndOutputPopulation();

                                        }

                                        CTtree.reuseExpectedValues(false);

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
//            }
        }
    }
}
