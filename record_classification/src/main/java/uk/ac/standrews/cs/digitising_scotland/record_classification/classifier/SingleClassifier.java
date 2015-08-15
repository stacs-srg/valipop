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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.ConsistentCodingChecker;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.util.tools.Logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class SingleClassifier extends Classifier {

    private Map<String, Double> confidence_map = new HashMap<>();

    public abstract void trainModel(final Bucket bucket);

    protected abstract Classification doClassify(String data);

    /**
     * Trains the classifier on the given gold standard records.
     *
     * @param bucket the training data
     */
    public void trainAndEvaluate(final Bucket bucket, final Random random) {

        Bucket real_training_records = bucket.randomSubset(random, 0.8);
        Bucket internal_evaluation_records = bucket.difference(real_training_records);

        trainModel(real_training_records);

        final Bucket classified_records = classify(internal_evaluation_records, false);

        final StrictConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_records, bucket, new ConsistentCodingChecker());
        final ClassificationMetrics classification_metrics = new ClassificationMetrics(confusion_matrix);

        confidence_map = classification_metrics.getPerClassF1();
    }

    public final Classification classify(String data) {

        Classification classification = doClassify(data);
        classification.setConfidence(getConfidence(classification.getCode()));
        return classification;
    }

    /**
     * Classifies a bucket of data items.
     * If a record in the given bucket cannot be classified, its classification is set to {@link Classification#UNCLASSIFIED}.
     *
     * @param bucket the data to be classified
     * @return a new bucket containing the classified data
     */
    public Bucket classify(final Bucket bucket) {

        return classify(bucket, true);
    }

    private Bucket classify(final Bucket bucket, boolean set_confidence) {

        Logging.setProgressIndicatorSteps(bucket.size());
        Logging.output(InfoLevel.VERBOSE, "Classifying...");

        final Bucket classified = new Bucket();

        for (Record record : bucket) {

            final String data = record.getData();

            Classification classification = classify(data);
            if (set_confidence) classification.setConfidence(getConfidence(classification.getCode()));
            classified.add(new Record(record.getId(), data, record.getOriginalData(), classification));

            Logging.progressStep();
        }

        return classified;
    }

    protected double getConfidence(String code) {

        if (confidence_map.containsKey(code)) {
            double confidence = confidence_map.get(code);
            return (Double.isNaN(confidence)) ? 0.0 : confidence;
        } else {
            return 0.0;
        }
    }
}
