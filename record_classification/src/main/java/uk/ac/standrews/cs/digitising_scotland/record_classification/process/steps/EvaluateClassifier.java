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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;

import java.time.*;
import java.util.*;

/**
 * Evaluates a classifier in the context of a classification process.
 * Stores the result of evaluation by setting the {@link Context#getConfusionMatrix() confusion matrix} and {@link Context#getClassificationMetrics() classification metrix} of the context.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public class EvaluateClassifier implements Step {

    private static final long serialVersionUID = -2181763269734136008L;
    private final InfoLevel verbosity;

    public EvaluateClassifier() {

        this(InfoLevel.NONE);
    }

    public EvaluateClassifier(InfoLevel verbosity) {

        this.verbosity = verbosity;
    }

    @Override
    public void perform(final Context context) throws Exception {

        final Classifier classifier = context.getClassifier();
        final Bucket gold_standard = context.getGoldStandard();
        final Bucket training_records = context.getTrainingRecords();

        final Bucket records_not_in_training = gold_standard.difference(training_records);
        final Bucket stripped_records = records_not_in_training.stripRecordClassifications();
        final Bucket evaluation_records = stripped_records.uniqueDataRecords();

        final Instant start = Instant.now();
        final Bucket classified_evaluation_records = classifier.classify(evaluation_records);
        final Duration evaluation_classification_time = Duration.between(start, Instant.now());
        context.setEvaluationClassificationTime(evaluation_classification_time);

        final StrictConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_evaluation_records, gold_standard, ConsistentCodingCleaner.CHECK);
        context.setConfusionMatrix(confusion_matrix);

        final ClassificationMetrics classification_metrics = new ClassificationMetrics(confusion_matrix);
        context.setClassificationMetrics(classification_metrics);

        if (verbosity.compareTo(InfoLevel.LONG_SUMMARY) >= 0) {

            final Set<String> unique_training = extractStrings(context.getTrainingRecords().uniqueDataRecords());
            final Set<String> unique_evaluation = extractStrings(evaluation_records);

            final int count = countEvaluationStringsNotInTrainingSet(unique_training, unique_evaluation);

            System.out.println("\n----------------------------------\n\n");
            System.out.format("total records              : %s%n", gold_standard.size());
            System.out.format("records used for training  : %s (%s unique)%n", training_records.size(), unique_training.size());
            System.out.format("records used for evaluation: %s (%s unique, %s not in training set)%n", stripped_records.size(), unique_evaluation.size(), count);
            System.out.println();

            context.getClassificationMetrics().printMetrics(verbosity);

            System.out.println();
            System.out.format("training time              : %s%n", context.getTrainingTime());
            System.out.format("classification time        : %s%n", context.getEvaluationClassificationTime());

            System.out.println("----------------------------------");
        }
    }

    private static Set<String> extractStrings(final Bucket bucket) {

        Set<String> strings = new HashSet<>();
        for (Record record : bucket) {
            strings.add(record.getData());
        }
        return strings;
    }

    private static int countEvaluationStringsNotInTrainingSet(final Set<String> unique_training, final Set<String> unique_evaluation) {

        int count = 0;
        for (String evaluation_string : unique_evaluation) {
            if (!unique_training.contains(evaluation_string))
                count++;
        }
        return count;
    }
}
