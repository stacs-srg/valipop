/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.population;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.utils.ProcessArgs;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.ContingencyTableValidator;

/**
 * Searches for recovery factors in parallel using Apache Spark 
 * 
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 */
public class DistributedFactorSearch {
    public static void main(final String[] args) throws InterruptedException {
        final String[] pArgs = ProcessArgs.process(args, "FACTOR_SEARCH_PRECISION");
        if (!ProcessArgs.check(pArgs, "FACTOR_SEARCH_PRECISION")) {
            System.err.println("Incorrect arguments given");
            throw new Error("Incorrect arguments given");
        }

        final Path dataFiles = Paths.get(pArgs[0]);
        final int seedSize = Integer.valueOf(pArgs[1]);
        final String runPurpose = pArgs[2];
        final int numberOfRunsPerSim = Integer.valueOf(pArgs[3]);

        final String rfsArg = pArgs[4];
        final String prfsArg = pArgs[5];

        final Path resultsDir = Paths.get(pArgs[6]);
        final Path summaryResultsDir = Paths.get(pArgs[7]);

        final double[] precisions = toDoubleArray(pArgs[8]);

        final Path projectPath = Paths.get(pArgs[9]);

        final double[] rfs = toDoubleArray(rfsArg);
        final double[] prfs = toDoubleArray(prfsArg);

        final SparkConf conf = new SparkConf().setAppName("valipop");
        final JavaSparkContext sc = new JavaSparkContext(conf);

        final List<ModelInput> inputs = generateInputs(seedSize, rfs, prfs, precisions, dataFiles, numberOfRunsPerSim, runPurpose, resultsDir, summaryResultsDir, projectPath);

        if (!inputs.isEmpty()) {
            final ModelInput i = inputs.get(0);

            // Creates a config for the sole purpose of creating the directory structure
            new Config(i.tS, i.t0, i.tE, i.size, Path.of(i.dataFiles), Path.of(i.summaryResultsLocation), i.runPurpose, Path.of(i.summaryResultsLocation));
        }

        System.out.println("Generated " + inputs.size() + " configs");

        final JavaRDD<ModelInput> df = sc.parallelize(
            inputs,
            inputs.size()
        );

        df
            .map(DistributedFactorSearch::runModel)
            .map(DistributedFactorSearch::validateModel)
            .collect()
            .forEach(DistributedFactorSearch::outputSummary);

        sc.close();
    }

    private static class ModelInput implements Serializable {
        public LocalDate tS;
        public LocalDate t0;
        public LocalDate tE;
        public int size;
        public String dataFiles;
        public String resultLocation;
        public String runPurpose;
        public String summaryResultsLocation;
        public String projectPath;
        
        public double precision;
        public double set_up_br;
        public double set_up_dr;
        public double rf;
        public double prf;
        public Period input_width;
        public Period minBirthSpacing;
        
        public ModelInput(
            final LocalDate tS,
            final LocalDate t0,
            final LocalDate tE,
            final int size,
            final String dataFiles,
            final String resultLocation,
            final String runPurpose,
            final String summaryResultsLocation,
            final String projectPath,

            final double precision,
            final double set_up_br,
            final double set_up_dr,
            final double rf,
            final double prf,
            final Period input_width,
            final Period minBirthSpacing
        ) {
            this.tS               = tS;
            this.t0               = t0;
            this.tE               = tE;
            this.size             = size;
            this.dataFiles        = dataFiles;
            this.resultLocation   = resultLocation;
            this.runPurpose       = runPurpose;
            this.summaryResultsLocation  = summaryResultsLocation;
            this.projectPath      = projectPath;
            this.precision        = precision;
            this.set_up_br        = set_up_br;
            this.set_up_dr        = set_up_dr;
            this.rf               = rf;
            this.prf              = prf;
            this.input_width      = input_width;
            this.minBirthSpacing   = minBirthSpacing;
        }
    }

    private static class ModelOutput implements Serializable {
        public int age;
        public SerializableSummaryRow summaryRow;

        ModelOutput(final int age, final SerializableSummaryRow summaryRow) {
            this.age = age;
            this.summaryRow = summaryRow;
        }
    }

    // --- Distributed Operations ---
    private static ModelOutput runModel(final ModelInput i) {
        final Config config = new Config(i.tS, i.t0, i.tE, i.size, Paths.get(i.dataFiles), Paths.get(i.resultLocation), i.runPurpose, Paths.get(i.summaryResultsLocation));

        config.setCTtreePrecision(i.precision);
        config.setSetupBirthRate(i.set_up_br);
        config.setSetupDeathRate(i.set_up_dr);
        config.setRecoveryFactor(i.rf);
        config.setProportionalRecoveryFactor(i.prf);
        config.setInputWidth(i.input_width);
        config.setMinBirthSpacing(i.minBirthSpacing);
        config.setProjectPath(Paths.get(i.projectPath));

        final OBDModel model = new OBDModel(config);

        try {
            System.out.println("Simulating the model");
            model.runSimulation();
            System.out.println("Analysing the model");
            model.analyseAndOutputPopulation(false);
            System.out.println("Complete for rf: " +config.getRecoveryFactor() + ", rpf: " + config.getProportionalRecoveryFactor());
        } catch(final Exception e) {
            System.out.println("Given rf: " + config.getRecoveryFactor() + ", rpf: " + config.getProportionalRecoveryFactor());

            throw e;
        }

        final SummaryRow summaryRow = model.getSummaryRow();
        final int maxBirthingAge = model.getDesiredPopulationStatistics().getOrderedBirthRates(Year.of(0)).getLargestLabel().getValue();

        return new ModelOutput(maxBirthingAge, summaryRow.toSerialized());
    }

    private static SerializableSummaryRow validateModel(final ModelOutput result) throws PreEmptiveOutOfMemoryWarning, IOException, StatsException {

        final Config config = new Config(result.summaryRow.config);
        final int maxMotherBirthAge = result.age;
        final int startYear = config.getT0().getYear();
        final int endYear = config.getTE().getYear();

        final SummaryRow summaryRow = new SummaryRow(result.summaryRow);

        final ProgramTimer statsTimer = new ProgramTimer();
        final double score = new ContingencyTableValidator(config.getRunPath(), maxMotherBirthAge, startYear, endYear).getValidationScore();

        summaryRow.setV(score);
        summaryRow.setStatsRunTime(statsTimer.getRunTimeSeconds());

        return summaryRow.toSerialized();
    }

    private static void outputSummary(final SerializableSummaryRow sr) {
        final SummaryRow summaryRow = new SummaryRow(sr);
        summaryRow.outputSummaryRowToFile();
    }

    private static double[] toDoubleArray(final String rfsArg) {

        final String[] split = rfsArg.split(",");
        final double[] ret = new double[split.length];

        int c = 0;

        for (final String s : split)
            ret[c++] = Double.parseDouble(s);

        return ret;
    }

    private static List<ModelInput> generateInputs(final int size0, final double[] recovery_factors, final double[] proportional_recovery_factors, final double[] precisions, final Path dataFiles, final int numberOfRunsPerSim, final String runPurpose, final Path resultsDir, final Path summaryResultsDir, final Path projectPath) throws InterruptedException {
        final LocalDate tS = LocalDate.of(1599, 1, 1);
        final LocalDate t0 = LocalDate.of(1855, 1, 1);
        final LocalDate tE = LocalDate.of(2015, 1, 1);

        final double set_up_br = 0.0233;
        final double set_up_dr = 0.0233;

        final Period[] input_widths = new Period[]{Period.ofYears(10)};
        final Period[] minBirthSpacings = new Period[]{Period.ofDays(147)};
        final int[] t0_pop_sizes = new int[]{size0};

        final List<ModelInput> inputs = new ArrayList<>();

        for (final double precision : precisions) {
            for (final int size : t0_pop_sizes) {
                for (final double recovery_factor : recovery_factors) {
                    for (final double proportional_recovery_factor : proportional_recovery_factors) {
                        for (final Period input_width : input_widths) {
                            for (final Period minBirthSpacing : minBirthSpacings) {
                                for (int n = 0; n < numberOfRunsPerSim; n++) {
                                    final ModelInput input = new ModelInput(
                                        tS,
                                        t0,
                                        tE,
                                        size,
                                        dataFiles.toString(),
                                        resultsDir.toString(),
                                        runPurpose,
                                        summaryResultsDir.toString(),
                                        projectPath.toString(),
                                        precision,
                                        set_up_br,
                                        set_up_dr,
                                        recovery_factor,
                                        proportional_recovery_factor,
                                        input_width,
                                        minBirthSpacing
                                    );
                                    inputs.add(input);
                                }
                            }
                        }
                    }
                }
            }
        }

        return inputs;
    }
}