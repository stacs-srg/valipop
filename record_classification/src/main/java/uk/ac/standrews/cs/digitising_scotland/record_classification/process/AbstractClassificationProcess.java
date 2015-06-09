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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ConcreteClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.util.csv.DataSet;
import uk.ac.standrews.cs.util.tables.TableGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractClassificationProcess implements ClassificationProcess {

    private static final int TRAINING_DATA_FILE_ARG_POS = 0;
    private static final int TRAINING_RATIO_ARG_POS = 1;

    private InputStreamReader gold_standard_data_reader;
    private double training_ratio;
    private InfoLevel info_level = InfoLevel.NONE;
    private Classifier classifier;

    private Bucket all_records;
    private Bucket training_records;
    private Bucket evaluation_records;
    private Bucket classified_evaluation_records;

    public AbstractClassificationProcess() {

        classifier = getClassifier();
    }

    public AbstractClassificationProcess(InputStreamReader gold_standard_data_reader, double training_ratio) {

        this();

        setGoldStandardData(gold_standard_data_reader);
        setTrainingRatio(training_ratio);
    }

    public AbstractClassificationProcess(String[] args) throws IOException, InvalidArgException {

        this(getTrainingDataFileFromArgs(args), getTrainingRatioFromArgs(args));
    }

    public abstract Classifier getClassifier();

    public void setTrainingRatio(double training_ratio) {

        this.training_ratio = training_ratio;
    }

    public void setInfoLevel(InfoLevel info_level) {

        this.info_level = info_level;
    }

    public void setGoldStandardData(InputStreamReader gold_standard_data_reader) {

        this.gold_standard_data_reader = gold_standard_data_reader;
    }

    public void configureRecords() throws IOException, InputFileFormatException {

        readInGoldStandardRecords();
        splitTrainingAndEvaluationRecords();
    }

    public void performTraining() {

        classifier.train(training_records);
    }

    public void performClassification() {

        classified_evaluation_records = classifier.classify(evaluation_records);
    }

    public ClassificationMetrics evaluateClassification() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ConfusionMatrix matrix = new StrictConfusionMatrix(classified_evaluation_records, all_records, false);

        ClassificationMetrics metrics = new ConcreteClassificationMetrics(matrix);

        if (info_level == InfoLevel.VERBOSE) {

            System.out.println();
            System.out.println("number of evaluation records: " + evaluation_records.size());
            System.out.println("number of gold standard records: " + all_records.size());
            System.out.println();

            metrics.printMetrics(info_level);
        }

        return metrics;
    }

    public void trainClassifyAndEvaluate(int number_of_repetitions) throws IOException, InputFileFormatException, UnclassifiedGoldStandardRecordException, UnknownDataException, InvalidCodeException, InconsistentCodingException {

        readInGoldStandardRecords();

        List<ClassificationMetrics> results = new ArrayList<>();
        for (int i = 0; i < number_of_repetitions; i++) {
            results.add(trainClassifyAndEvaluate());
        }

        summariseResults(results);
    }

    private void readInGoldStandardRecords() throws InputFileFormatException, IOException {

        all_records = new Bucket(gold_standard_data_reader);
    }

    private void splitTrainingAndEvaluationRecords() {

        training_records = extractRandomSubset(all_records, training_ratio);
        Bucket records_not_in_training_set = difference(all_records, training_records);
        evaluation_records = stripClassifications(records_not_in_training_set);

        if (info_level == InfoLevel.VERBOSE) {
            System.out.println("Training records:");
            System.out.println(training_records);
            System.out.println();

            System.out.println("Evaluation records:");
            System.out.println(evaluation_records);
            System.out.println();
        }
    }

    private static InputStreamReader getTrainingDataFileFromArgs(final String[] args) throws InvalidArgException, IOException {

        if (args.length <= TRAINING_DATA_FILE_ARG_POS) {
            throw new InvalidArgException("training data file argument missing");
        }

        Path path = Paths.get(args[TRAINING_DATA_FILE_ARG_POS]);

        try {
            InputStream input_stream = Files.newInputStream(path);
            return new InputStreamReader(input_stream);

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

    private Bucket extractRandomSubset(final Bucket bucket, final double training_ratio) {

        Bucket subset_bucket = new Bucket();

        for (Record record : bucket) {
            if (Math.random() < training_ratio) {
                subset_bucket.add(record);
            }
        }

        return subset_bucket;
    }

    private Bucket difference(final Bucket larger_bucket, final Bucket smaller_bucket) {

        Bucket difference_bucket = new Bucket();

        for (Record record : larger_bucket) {
            if (!smaller_bucket.contains(record)) {
                difference_bucket.add(record);
            }
        }

        return difference_bucket;
    }

    private Bucket stripClassifications(final Bucket bucket) {

        Bucket unclassified_bucket = new Bucket();

        for (Record record : bucket) {
            unclassified_bucket.add(new Record(record.getId(), record.getData(), null));
        }

        return unclassified_bucket;
    }

    private ClassificationMetrics trainClassifyAndEvaluate() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        splitTrainingAndEvaluationRecords();
        performTraining();
        performClassification();

        return evaluateClassification();
    }

    private void summariseResults(List<ClassificationMetrics> results) throws IOException {

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

        String table_caption = "Aggregate classifier performance (" + results.size() + " repetition" + (results.size() > 1 ? "s" : "") + ")";
        String first_column_heading = "classifier";

        TableGenerator table_generator = new TableGenerator(Collections.singletonList("exact-match"), Collections.singletonList(data_set), System.out, table_caption, first_column_heading, true, '\t');

        if (info_level != InfoLevel.NONE) {

            table_generator.printTable();
        }
    }
}
