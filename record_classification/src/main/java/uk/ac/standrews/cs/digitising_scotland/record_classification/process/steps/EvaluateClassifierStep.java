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
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.Step;
import uk.ac.standrews.cs.util.tools.Formatting;

import java.time.*;
import java.util.*;

/**
 * Evaluates a classifier in the context of a classification process.
 * Stores the result of evaluation by setting the {@link ClassificationContext#getConfusionMatrix() confusion matrix} and {@link ClassificationContext#getClassificationMetrics() classification metrix} of the context.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public class EvaluateClassifierStep implements Step {

    private static final long serialVersionUID = -2181763269734136008L;

    public EvaluateClassifierStep() {
    }

    @Override
    public void perform(final ClassificationContext context)  {

        final Bucket gold_standard_records = context.getGoldStandardRecords();
        final Bucket evaluation_records = context.getEvaluationRecords();
        final Bucket stripped_records = evaluation_records.stripRecordClassifications();

        final Instant start = Instant.now();
        final Bucket classified_records = context.getClassifier().classify(stripped_records);
        context.setClassificationTime(Duration.between(start, Instant.now()));

        final StrictConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_records, gold_standard_records, new ConsistentCodingChecker());
        final ClassificationMetrics classification_metrics = new ClassificationMetrics(confusion_matrix);

        context.setConfusionMatrix(confusion_matrix);
        context.setClassificationMetrics(classification_metrics);

        if (context.getVerbosity().compareTo(InfoLevel.LONG_SUMMARY) >= 0) {
            printClassificationDetails(context);
        }
    }

    private void printClassificationDetails(ClassificationContext context) {

        final Classifier classifier = context.getClassifier();
        final Bucket evaluation_records = context.getEvaluationRecords();
        final Bucket stripped_records = evaluation_records.stripRecordClassifications();

        final Set<String> unique_training = extractStrings(context.getTrainingRecords().uniqueDataRecords());
        final Set<String> unique_evaluation = extractStrings(evaluation_records);

        final int count = countEvaluationStringsNotInTrainingSet(unique_training, unique_evaluation);
        final int training_records_size = context.getTrainingRecords().size();

        System.out.println("\n----------------------------------\n");
        System.out.println("classifier: " + classifier.getName());
        System.out.println();
        System.out.format("total records              : %s%n", Formatting.format(training_records_size + evaluation_records.size()));
        System.out.format("records used for training  : %s (%s unique)%n", Formatting.format(training_records_size), Formatting.format(unique_training.size()));
        System.out.format("records used for evaluation: %s (%s unique, %s not in training set)%n", Formatting.format(stripped_records.size()), Formatting.format(unique_evaluation.size()), Formatting.format(count));
        System.out.println();

        context.getClassificationMetrics().printMetrics(context.getVerbosity());

        System.out.println();
        System.out.format("training time              : %s%n", Formatting.format(context.getTrainingTime()));
        System.out.format("classification time        : %s%n", Formatting.format(context.getClassificationTime()));

        System.out.println("----------------------------------");
    }

    private static Set<String> extractStrings(final Bucket bucket) {

        final Set<String> strings = new HashSet<>();

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
