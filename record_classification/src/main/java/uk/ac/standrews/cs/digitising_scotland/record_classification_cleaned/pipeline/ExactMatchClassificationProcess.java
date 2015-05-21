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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.Bucket2;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.ExactMatchClassifier2;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.Record2;

import java.io.File;
import java.io.IOException;

public class ExactMatchClassificationProcess {//implements ClassificationProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactMatchClassificationProcess.class);

    private static final int TRAINING_DATA_FILE_ARG_POS = 0;
    private static final int TRAINING_RATIO_ARG_POS = 2;

    File training_data_file;
    double training_ratio;

    /**
     * Entry method for training and classifying a batch of records into
     * multiple codes.
     *
     * @param args <file1> training file <file2> file to classify
     * @throws Exception If exception occurs
     */
    public static void main(final String[] args) throws Exception {

        new ExactMatchClassificationProcess(args).performClassification();
    }

    public ExactMatchClassificationProcess(String[] args) throws IOException, InvalidArgException {

        LOGGER.info("Running with args: {}", (Object) args);

        training_data_file = getTrainingDataFileFromArgs(args);
        training_ratio = getTrainingRatioFromArgs(args);
    }

    public void performClassification() throws Exception {

        Bucket2 all_records = new Bucket2(training_data_file);
        Bucket2 training_records = extractRandomSubset(all_records, training_ratio);
        Bucket2 evaluation_records = discardClassifications(difference(all_records, training_records));

        ExactMatchClassifier2 exact_match_classifier = new ExactMatchClassifier2();
        exact_match_classifier.train(training_records);

        Bucket2 classified_evaluation_records = exact_match_classifier.classify(evaluation_records);

        ClassificationMetrics metrics = new ClassificationMetrics(classified_evaluation_records, all_records);

        metrics.printMetrics();
    }

    private static File getTrainingDataFileFromArgs(final String[] args) throws InvalidArgException {

        if (args.length <= TRAINING_DATA_FILE_ARG_POS) {
            throw new InvalidArgException("training data file argument missing");
        }

        File training_data_file = new File(args[TRAINING_DATA_FILE_ARG_POS]);

        if (!training_data_file.exists()) {
            throw new InvalidArgException(training_data_file.getAbsolutePath() + " does not exist");
        }

        return training_data_file;
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

    private Bucket2 extractRandomSubset(final Bucket2 bucket, final double training_ratio) {

        Bucket2 subset_bucket = new Bucket2();

        for (Record2 record : bucket) {
            if (Math.random() < training_ratio) {
                subset_bucket.add(record);
            }
        }

        return subset_bucket;
    }

    private Bucket2 difference(final Bucket2 larger_bucket, final Bucket2 smaller_bucket) {

        Bucket2 difference_bucket = new Bucket2();

        for (Record2 record : larger_bucket) {
            if (!smaller_bucket.contains(record)) {
                difference_bucket.add(record);
            }
        }

        return difference_bucket;
    }

    private Bucket2 discardClassifications(final Bucket2 bucket) {

        Bucket2 unclassified_bucket = new Bucket2();

        for (Record2 record : bucket) {
            unclassified_bucket.add(new Record2(record.getId(), record.getData(), null));
        }

        return unclassified_bucket;
    }
}
