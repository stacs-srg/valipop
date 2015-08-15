/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.experiments;

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.generic.Experiment;
import uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.specific.ExactMatchAndStringSimilarityExperiment;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.util.tools.FileManipulation;
import uk.ac.standrews.cs.util.tools.Logging;

import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class ExperimentTest {

    public static final String CODED_DATA_1K_FILE_NAME = "coded_data_1K.csv";
    public static final int NUMBER_OF_REPETITIONS = 5;

    private Experiment experiment;
    private List<Experiment.ClassifierResults> experiment_results;

    @Before
    public void setup() throws Exception {

        Logging.setInfoLevel(InfoLevel.NONE);

        experiment = new ExactMatchAndStringSimilarityExperiment();

        Path path = FileManipulation.getResourcePath(AbstractClassificationProcessTest.class, CODED_DATA_1K_FILE_NAME);

        experiment.setGoldStandardFiles(Arrays.asList(path));
        experiment.setRepetitions(NUMBER_OF_REPETITIONS);

        experiment_results = experiment.runExperiment();
    }

    @Test
    public void resultsContainDataForCorrectNumberOfClassifiers() throws Exception {

        assertEquals(experiment_results.size(), experiment.getProcesses().size());
    }

    @Test
    public void numberOfTrainingRecordsVariesAcrossRepetitionsOfEachExperiment() throws Exception {

        for (Experiment.ClassifierResults result : experiment_results) {

            List<Integer> training_bucket_sizes_across_repetitions = new ArrayList<>();

            for (ClassificationContext context : result.getContexts()) {

                training_bucket_sizes_across_repetitions.add(context.getTrainingRecords().size());
            }

            assertNotAllSame(training_bucket_sizes_across_repetitions);
        }
    }

    @Test
    public void numbersOfTrainingRecordsInEachRepetitionAreSameAcrossExperiments() throws Exception {

        List<List<Integer>> training_bucket_sizes = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_REPETITIONS; i++) {
            training_bucket_sizes.add(new ArrayList<>());
        }

        for (Experiment.ClassifierResults result : experiment_results) {

            int repetition_number = 0;
            for (ClassificationContext context : result.getContexts()) {

                training_bucket_sizes.get(repetition_number++).add(context.getTrainingRecords().size());
            }
        }

        for (List<Integer> training_records_sizes_for_repetition : training_bucket_sizes) {
            assertAllSame(training_records_sizes_for_repetition);
        }
    }

    private void assertAllSame(List<Integer> numbers) {

        for (int i : numbers) {
            if (i != numbers.get(0)) {
                fail();
            }
        }
    }

    private void assertNotAllSame(List<Integer> numbers) {

        Set<Integer> unique_numbers = new HashSet<>();
        for (int i : numbers) {

            unique_numbers.add(i);
        }
        if (numbers.size() > 1) assertTrue(unique_numbers.size() > 1);
    }
}
