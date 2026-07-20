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

    public static void main(final String[] args) throws InterruptedException, IOException {

        final String[] pArgs = ProcessArgs.process(args, "FACTOR_SEARCH_PRECISION");
        if (!ProcessArgs.check(pArgs, "FACTOR_SEARCH_PRECISION")) {
            System.err.println("Incorrect arguments given");
            throw new Error("Incorrect arguments given");
        }

        final Path dataFiles = Paths.get(pArgs[0]);
        final int seedSize = Integer.parseInt(pArgs[1]);
        final String runPurpose = pArgs[2];
        final int numberOfRunsPerSim = Integer.parseInt(pArgs[3]);

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
            new Config(i.initialisation_start, i.simulation_start, i.simulation_end, i.size, Path.of(i.dataFiles), Path.of(i.summaryResultsLocation), i.runPurpose, Path.of(i.summaryResultsLocation));
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

        public LocalDate initialisation_start;
        public LocalDate simulation_start;
        public LocalDate simulation_end;
        public int size;
        public String dataFiles;
        public String resultLocation;
        public String runPurpose;
        public String summaryResultsLocation;
        public String projectPath;
        
        public double precision;
        public double initialisation_birth_rate;
        public double initialisation_death_rate;
        public double rf;
        public double prf;
        public Period distribution_granularity;
        public Period minBirthSpacing;
        
        public ModelInput(
            final LocalDate initialisation_start,
            final LocalDate simulation_start,
            final LocalDate simulation_end,
            final int size,
            final String dataFiles,
            final String resultLocation,
            final String runPurpose,
            final String summaryResultsLocation,
            final String projectPath,

            final double precision,
            final double initialisation_birth_rate,
            final double initialisation_death_rate,
            final double rf,
            final double prf,
            final Period distribution_granularity,
            final Period minBirthSpacing
        ) {
            this.initialisation_start               = initialisation_start;
            this.simulation_start               = simulation_start;
            this.simulation_end               = simulation_end;
            this.size             = size;
            this.dataFiles        = dataFiles;
            this.resultLocation   = resultLocation;
            this.runPurpose       = runPurpose;
            this.summaryResultsLocation  = summaryResultsLocation;
            this.projectPath      = projectPath;
            this.precision        = precision;
            this.initialisation_birth_rate        = initialisation_birth_rate;
            this.initialisation_death_rate        = initialisation_death_rate;
            this.rf               = rf;
            this.prf              = prf;
            this.distribution_granularity      = distribution_granularity;
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
    private static ModelOutput runModel(final ModelInput i) throws IOException {

        final Config config = new Config(i.initialisation_start, i.simulation_start, i.simulation_end, i.size, Paths.get(i.dataFiles), Paths.get(i.resultLocation), i.runPurpose, Paths.get(i.summaryResultsLocation));

        config.setCTtreePrecision(i.precision);
        config.setSetupBirthRate(i.initialisation_birth_rate);
        config.setSetupDeathRate(i.initialisation_death_rate);
        config.setRecoveryFactor(i.rf);
        config.setProportionalRecoveryFactor(i.prf);
        config.setDistributionGranularity(i.distribution_granularity);
        config.setMinBirthSpacing(i.minBirthSpacing);
        config.setProjectPath(Paths.get(i.projectPath));

        final OBDModel model = new OBDModel(config);

        System.out.println("Simulating the model");
        model.runSimulation();
        System.out.println("Analysing the model");
        model.analyseAndOutputPopulation(false);
        System.out.println("Complete for rf: " +config.getRecoveryFactor() + ", rpf: " + config.getProportionalRecoveryFactor());

        final SummaryRow summaryRow = model.getSummaryRow();
        final int maxBirthingAge = model.getDesiredPopulationStatistics().getOrderedBirthRates(Year.of(0)).getLargestLabel().getValue();

        return new ModelOutput(maxBirthingAge, summaryRow.toSerialized());
    }

    private static SerializableSummaryRow validateModel(final ModelOutput result) throws PreEmptiveOutOfMemoryWarning, IOException {

        final Config config = new Config(result.summaryRow.config);
        final SummaryRow summaryRow = new SummaryRow(result.summaryRow);

        final ProgramTimer statsTimer = new ProgramTimer();
        final double score = new ContingencyTableValidator(config).getValidationScore();

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
        final LocalDate initialisation_start = LocalDate.of(1599, 1, 1);
        final LocalDate simulation_start = LocalDate.of(1855, 1, 1);
        final LocalDate simulation_end = LocalDate.of(2015, 1, 1);

        final double initialisation_birth_rate = 0.0233;
        final double initialisation_death_rate = 0.0233;

        final Period[] input_widths = new Period[]{Period.ofYears(10)};
        final Period[] minBirthSpacings = new Period[]{Period.ofDays(147)};
        final int[] t0_pop_sizes = new int[]{size0};

        final List<ModelInput> inputs = new ArrayList<>();

        for (final double precision : precisions) {
            for (final int size : t0_pop_sizes) {
                for (final double recovery_factor : recovery_factors) {
                    for (final double proportional_recovery_factor : proportional_recovery_factors) {
                        for (final Period distribution_granularity : input_widths) {
                            for (final Period minBirthSpacing : minBirthSpacings) {
                                for (int n = 0; n < numberOfRunsPerSim; n++) {
                                    final ModelInput input = new ModelInput(
                                        initialisation_start,
                                        simulation_start,
                                        simulation_end,
                                        size,
                                        dataFiles.toString(),
                                        resultsDir.toString(),
                                        runPurpose,
                                        summaryResultsDir.toString(),
                                        projectPath.toString(),
                                        precision,
                                        initialisation_birth_rate,
                                        initialisation_death_rate,
                                        recovery_factor,
                                        proportional_recovery_factor,
                                        distribution_granularity,
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