package uk.ac.standrews.cs.digitising_scotland.record_classification.process.experiments;

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExperimentTest {

    public static final String CODED_DATA_1K_FILE_NAME = "coded_data_1K.csv";
    public static final int NUMBER_OF_REPETITIONS = 5;

    private List<Experiment.ExperimentResult> experiment_results;

    @Before
    public void setup() throws Exception {

        Experiment experiment = new ExactMatchAndStringSimilarityExperiment();

        File resourceFile = FileManipulation.getResourceFile(AbstractClassificationProcessTest.class, CODED_DATA_1K_FILE_NAME);

        experiment.setGoldStandardFiles(Arrays.asList(resourceFile));
        experiment.setRepetitions(NUMBER_OF_REPETITIONS);
        experiment.setVerbosity(InfoLevel.NONE);

        experiment_results = experiment.getExperimentResults();
    }

    @Test
    public void resultsContainDataForTwoClassifiers() throws Exception {

        assertEquals(experiment_results.size(), 2);
    }

    @Test
    public void numberOfRecordsIsSameAcrossAllRepetitionsOfAllExperiments() throws Exception {

        List<Integer> overall_bucket_sizes_across_repetitions = new ArrayList<>();

        for (Experiment.ExperimentResult result : experiment_results) {

            for (ClassificationContext context : result.repetition_contexts) {

                final int training_records_size = context.getTrainingRecords().size();
                final int overall_records_size = context.getEvaluationRecords().size() + training_records_size;

                overall_bucket_sizes_across_repetitions.add(overall_records_size);
            }
        }

        assertAllSame(overall_bucket_sizes_across_repetitions);
    }

    @Test
    public void numberOfTrainingRecordsVariesAcrossRepetitionsOfEachExperiment() throws Exception {

        for (Experiment.ExperimentResult result : experiment_results) {

            List<Integer> training_bucket_sizes_across_repetitions = new ArrayList<>();

            for (ClassificationContext context : result.repetition_contexts) {

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

        for (Experiment.ExperimentResult result : experiment_results) {

            int repetition_number = 0;
            for (ClassificationContext context : result.repetition_contexts) {

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
