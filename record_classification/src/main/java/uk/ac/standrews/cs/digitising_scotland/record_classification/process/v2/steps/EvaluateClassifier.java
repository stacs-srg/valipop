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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

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

    @Override
    public void perform(final Context context) throws Exception {

        final Classifier classifier = context.getClassifier();
        final Bucket gold_standard = context.getGoldStandard();
        final Bucket training_records = context.getTrainingRecords();
        
        final Bucket records_not_in_training = difference(gold_standard, training_records);
        final Bucket stripped_records = stripClassifications(records_not_in_training);
        final Bucket evaluation_records = getUnique(stripped_records);
        final Bucket classified_evaluation_records = classifier.classify(evaluation_records);

        final StrictConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_evaluation_records, gold_standard, ConsistentCodingCleaner.CHECK);
        context.setConfusionMatrix(confusion_matrix);

        final ConcreteClassificationMetrics classification_metrics = new ConcreteClassificationMetrics(confusion_matrix);
        context.setClassificationMetrics(classification_metrics);
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
            unclassified_bucket.add(new Record(record.getId(), record.getData()));
        }

        return unclassified_bucket;
    }

    private static Bucket getUnique(final Bucket bucket) {

        Map<String, Record> unique_records = new HashMap<>();

        for (Record record : bucket) {
            unique_records.put(record.getData(), record);
        }

        final Bucket unique_bucket = new Bucket();

        for (Record record : unique_records.values()) {
            unique_bucket.add(record);
        }

        return unique_bucket;
    }
}
