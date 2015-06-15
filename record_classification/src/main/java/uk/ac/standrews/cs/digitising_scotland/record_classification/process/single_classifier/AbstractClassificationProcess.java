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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.single_classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ConcreteClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.ConsistentCodingCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InvalidArgException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.multiple_classifier.AbstractMultipleClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;

public abstract class AbstractClassificationProcess implements ClassificationProcess {

    private static final int TRAINING_DATA_FILE_ARG_POS = 0;
    private static final int TRAINING_RATIO_ARG_POS = 1;
    private static final int NUMBER_OF_REPETITIONS_ARG_POS = 2;

    private double training_ratio;
    private Cleaner cleaner;

    private int classification_run_counter = 1;
    private InfoLevel info_level = AbstractMultipleClassificationProcess.info_level;

    private final Bucket all_records;

    private List<ClassificationProcessState> classification_process_states;

    AbstractClassificationProcess(InputStreamReader gold_standard_data_reader, double training_ratio, int number_of_repetitions) throws Exception {

        cleaner = getCleaner();

        all_records = readInGoldStandardRecords(gold_standard_data_reader);

        this.training_ratio = training_ratio;

        classification_process_states = new ArrayList<>();

        for (int i = 0; i < number_of_repetitions; i++) {
            classification_process_states.add(new ClassificationProcessState());
        }
    }

    AbstractClassificationProcess(String[] args) throws Exception {

        this(getTrainingDataFileFromArgs(args), getTrainingRatioFromArgs(args), getNumberOfRepetitionsFromArgs(args));
    }

    protected abstract Classifier getClassifier();
    protected abstract Cleaner getCleaner();

    public void setInfoLevel(InfoLevel info_level) {

        this.info_level = info_level;
    }

    // Methods dealing with the overall process involving repeated runs.

    public DataSet trainClassifyAndEvaluate() throws Exception {

        List<ClassificationMetrics> results = new ArrayList<>();

        for (ClassificationProcessState state : classification_process_states) {

            trainClassifyAndEvaluate(state);
            results.add(state.metrics);
        }

        return getResultsAsDataSet(results);
    }

    // Methods dealing with a particular classification run.

    public void performTraining(ClassificationProcessState state) {

        state.classifier = getClassifier();

        state.classifier.train(state.training_records);
    }

    public void performClassification(ClassificationProcessState state) {

        state.classified_evaluation_records = state.classifier.classify(state.evaluation_records);
    }

    public List<ConfusionMatrix> getConfusionMatrices() throws Exception {

        List<ConfusionMatrix> matrices = new ArrayList<>();

        for (ClassificationProcessState state : classification_process_states) {
            matrices.add(state.confusion_matrix);
        }

        return matrices;
    }

    public List<ClassificationMetrics> getClassificationMetrics() throws Exception {

        List<ClassificationMetrics> metrics = new ArrayList<>();

        for (ClassificationProcessState state : classification_process_states) {
            metrics.add(state.metrics);
        }

        return metrics;
    }

    private void calculateClassificationMetrics(ClassificationProcessState state) throws Exception {

        state.metrics = new ConcreteClassificationMetrics(state.confusion_matrix);
    }

    private void calculateConfusionMatrix(ClassificationProcessState state) throws Exception {

        state.confusion_matrix = new StrictConfusionMatrix(state.classified_evaluation_records, all_records, ConsistentCodingCleaner.CHECK);
    }

    private Bucket readInGoldStandardRecords(InputStreamReader gold_standard_data_reader) throws Exception {

        Bucket records = new Bucket(gold_standard_data_reader);
        return cleaner.clean(records);
    }

    private void trainClassifyAndEvaluate(ClassificationProcessState state) throws Exception {

        splitTrainingAndEvaluationRecords(state);
        performTraining(state);
        performClassification(state);

        calculateConfusionMatrix(state);
        calculateClassificationMetrics(state);

        printInfo(state, info_level);
    }

    private void splitTrainingAndEvaluationRecords(ClassificationProcessState state) {

        state.training_records = extractRandomSubset(all_records, training_ratio);

        Bucket records_not_in_training_set = difference(all_records, state.training_records);
        state.stripped_records = stripClassifications(records_not_in_training_set);

        state.evaluation_records = getUnique(state.stripped_records);
    }

    private static Set<String> extractStrings(Bucket bucket) {

        Set<String> strings = new HashSet<>();
        for (Record record : bucket) {
            strings.add(record.getData());
        }
        return strings;
    }

    private static InputStreamReader getTrainingDataFileFromArgs(final String[] args) throws InvalidArgException, IOException {

        if (args.length <= TRAINING_DATA_FILE_ARG_POS) {
            throw new InvalidArgException("training data file argument missing");
        }

        Path path = Paths.get(args[TRAINING_DATA_FILE_ARG_POS]);

        try {
            return FileManipulation.getInputStreamReader(path);

        } catch (NoSuchFileException e) {
            throw new IOException("cannot open training file: " + path);
        }
    }

    private static double getTrainingRatioFromArgs(final String[] args) throws InvalidArgException {

        // TODO rewrite args handling using Apache Commons CLI2

        if (args.length <= TRAINING_RATIO_ARG_POS) {
            throw new InvalidArgException("training ratio argument missing");
        }

        String training_ratio_arg = args[TRAINING_RATIO_ARG_POS];

        try {
            double training_ratio = Double.valueOf(training_ratio_arg);
            if (training_ratio > 0 && training_ratio < 1) {
                return training_ratio;
            }
            throw new InvalidArgException("invalid training ratio: " + training_ratio);

        } catch (NumberFormatException e) {
            throw new InvalidArgException("invalid training ratio: " + training_ratio_arg);
        }
    }

    private static int getNumberOfRepetitionsFromArgs(final String[] args) throws InvalidArgException {

        // TODO rewrite args handling using Apache Commons CLI2

        if (args.length <= NUMBER_OF_REPETITIONS_ARG_POS) {
            throw new InvalidArgException("training ratio argument missing");
        }

        String number_of_repetitions_arg = args[NUMBER_OF_REPETITIONS_ARG_POS];

        try {
            int number_of_repetitions = Integer.valueOf(number_of_repetitions_arg);
            if (number_of_repetitions > 0) {
                return number_of_repetitions;
            }
            throw new InvalidArgException("invalid number of repetitions: " + number_of_repetitions);

        } catch (NumberFormatException e) {
            throw new InvalidArgException("invalid number of repetitions: " + number_of_repetitions_arg);
        }
    }

    private static Bucket extractRandomSubset(final Bucket bucket, final double selection_probability) {

        Bucket subset_bucket = new Bucket();

        for (Record record : bucket) {
            if (Math.random() < selection_probability) {
                subset_bucket.add(record);
            }
        }

        return subset_bucket;
    }

    private static Bucket difference(final Bucket larger_bucket, final Bucket smaller_bucket) {

        Bucket difference_bucket = new Bucket();

        for (Record record : larger_bucket) {
            if (!smaller_bucket.contains(record)) {
                difference_bucket.add(record);
            }
        }

        return difference_bucket;
    }

    private static Bucket stripClassifications(final Bucket bucket) {

        Bucket unclassified_bucket = new Bucket();

        for (Record record : bucket) {
            unclassified_bucket.add(new Record(record.getId(), record.getData(), null));
        }

        return unclassified_bucket;
    }

    private static Bucket getUnique(Bucket bucket) {

        Map<String, Record> unique_records = new HashMap<>();

        for (Record record : bucket) {
            unique_records.put(record.getData(), record);
        }

        Bucket unique_bucket = new Bucket();

        for (Record record : unique_records.values()) {
            unique_bucket.add(record);
        }

        return unique_bucket;
    }

    private static DataSet getResultsAsDataSet(List<ClassificationMetrics> results) {

        // Need to wrap the list to make it mutable so that the first-column label can be added.
        DataSet data_set = new DataSet(new ArrayList<>(Arrays.asList("macro-precision", "macro-recall", "macro-accuracy", "macro-F1", "micro-precision", "micro-recall", "micro-accuracy", "micro-F1")));

        for (ClassificationMetrics metrics : results) {
            data_set.addRow(Arrays.asList(
                    String.valueOf(metrics.getMacroAveragePrecision()),
                    String.valueOf(metrics.getMacroAverageRecall()),
                    String.valueOf(metrics.getMacroAverageAccuracy()),
                    String.valueOf(metrics.getMacroAverageF1()),
                    String.valueOf(metrics.getMicroAveragePrecision()),
                    String.valueOf(metrics.getMicroAverageRecall()),
                    String.valueOf(metrics.getMicroAverageAccuracy()),
                    String.valueOf(metrics.getMicroAverageF1())
            ));
        }
        return data_set;
    }

    private void printInfo(ClassificationProcessState state, InfoLevel info_level) {

        if (info_level != InfoLevel.NONE) {

            Set<String> unique_training = extractStrings(getUnique(state.training_records));
            Set<String> unique_evaluation = extractStrings(state.evaluation_records);

            int count = 0;
            for (String evaluation_string : unique_evaluation) {
                if (!unique_training.contains(evaluation_string)) count++;
            }

            System.out.println("\n-----------------------------");

            System.out.println("classification run " + classification_run_counter++ + "\n");


            System.out.println("total records              : " + format(all_records.size()));
            System.out.println("records used for training  : " + format(state.training_records.size()) + " (" + format(unique_training.size()) + " unique)");
            System.out.println("records used for evaluation: " + format(state.stripped_records.size()) + " (" + format(unique_evaluation.size()) + " unique, " + format(count) + " not in training set)");
            System.out.println();

            state.metrics.printMetrics(info_level);

            System.out.println("-----------------------------");
        }
    }

    private String format(int i) {

        return NumberFormat.getIntegerInstance().format(i);
    }

    private class ClassificationProcessState {

        Bucket stripped_records;
        Bucket training_records;
        Bucket evaluation_records;
        Bucket classified_evaluation_records;

        Classifier classifier;
        ConfusionMatrix confusion_matrix;
        ClassificationMetrics metrics;
    }
}
