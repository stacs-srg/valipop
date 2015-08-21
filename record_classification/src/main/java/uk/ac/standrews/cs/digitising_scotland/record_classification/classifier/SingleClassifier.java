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
import uk.ac.standrews.cs.util.tables.ConfidenceIntervals;
import uk.ac.standrews.cs.util.tables.Means;

import java.util.*;

public abstract class SingleClassifier extends Classifier {

    private Map<String, Double> confidence_map = new HashMap<>();

    private static final int INTERNAL_EVALUATION_REPETITIONS = 3;

    public abstract void trainModel(final Bucket bucket);

    protected abstract void clearModel();
    protected abstract Classification doClassify(String data);

    public final void trainAndEvaluate(final Bucket bucket, final double internal_training_ratio, final Random random) {

        List<Map<String, Double>> confidence_maps = new ArrayList<>();

        for (int i = 0; i < INTERNAL_EVALUATION_REPETITIONS; i++) {

            // On the last iteration, this leaves the model trained.
            confidence_maps.add(singleTrainAndEvaluate(bucket, internal_training_ratio, random));
        }

        confidence_map = lowerConfidenceIntervalBoundaries(confidence_maps);
    }

    @Override
    public String getName() {

        return getClass().getSimpleName();
    }

    @Override
    public String toString() {

        return getName();
    }

    public final Classification classify(String data) {

        Classification classification = doClassify(data);
        setConfidence(classification, true);
        return classification;
    }

    public Bucket classify(final Bucket bucket) {

        return classify(bucket, true);
    }

    protected void setConfidence(Classification classification, boolean set_confidence) {

        if (set_confidence) classification.setConfidence(getConfidence(classification.getCode()));
    }

    protected double getConfidence(String code) {

        if (confidence_map.containsKey(code)) {

            double confidence = confidence_map.get(code);
            return (Double.isNaN(confidence)) ? 0.0 : confidence;

        } else {
            return 0.0;
        }
    }

    private Map<String, Double> lowerConfidenceIntervalBoundaries(List<Map<String, Double>> confidence_maps) {

        // NB two distinct uses of 'confidence' here...

        Map<String, Double> confidence_map = new HashMap<>();
        Set<String> all_classes = new HashSet<>();

        for (Map<String, Double> map : confidence_maps) {
            all_classes.addAll(map.keySet());
        }

        for (String class_name : all_classes) {

            List<Double> confidence_values_for_class = findConfidenceValuesForClass(class_name, confidence_maps);
            confidence_map.put(class_name, calculateLowerConfidenceIntervalBoundary(confidence_values_for_class));
        }

        return confidence_map;
    }

    private List<Double> findConfidenceValuesForClass(String class_name, List<Map<String, Double>> confidence_maps) {

        List<Double> values = new ArrayList<>();

        for (Map<String, Double> map : confidence_maps) {
            values.add(map.containsKey(class_name) ? map.get(class_name) : 0.0);
        }

        return values;
    }

    private Double calculateLowerConfidenceIntervalBoundary(List<Double> values) {

        return Math.max(0.0, Means.calculateMean(values) - ConfidenceIntervals.calculateConfidenceInterval(values));
    }

    private Map<String, Double> singleTrainAndEvaluate(Bucket bucket, double internal_training_ratio, Random random) {

        Bucket real_training_records = bucket.randomSubset(random, internal_training_ratio);
        Bucket internal_evaluation_records = bucket.difference(real_training_records);

        clearModel();
        trainModel(real_training_records);

        final Bucket classified_records = classify(internal_evaluation_records, false);

        final StrictConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_records, bucket, new ConsistentCodingChecker());
        final ClassificationMetrics classification_metrics = new ClassificationMetrics(confusion_matrix);

        return classification_metrics.getPerClassF1();
    }
}
