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

import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.*;

import java.util.HashMap;
import java.util.Map;

public class ClassificationMetrics {

    // http://nlp.stanford.edu/IR-book/html/htmledition/evaluation-of-text-classification-1.html#17469

    private ConfusionMatrix confusion_matrix;

    private final Map<String, Double> precision;
    private final Map<String, Double> recall;
    private final Map<String, Double> accuracy;
    private final Map<String, Double> f1;

    public ClassificationMetrics(ConfusionMatrix confusion_matrix) {

        this.confusion_matrix = confusion_matrix;

        precision = new HashMap<>();
        recall = new HashMap<>();
        accuracy = new HashMap<>();
        f1 = new HashMap<>();

        calculateMetrics();
    }

    public Map<String, Double> getPerClassPrecision() {

        return precision;
    }

    public Map<String, Double> getPerClassRecall() {

        return recall;
    }

    public Map<String, Double> getPerClassAccuracy() {

        return accuracy;
    }

    public Map<String, Double> getPerClassF1() {

        return f1;
    }

    public void printMetrics() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        printMetrics("precision", precision);
        printMetrics("recall", recall);
        printMetrics("accuracy", accuracy);
        printMetrics("F1", f1);
    }

    private void printMetrics(String label, Map<String, Double> metrics) {

        System.out.println(label + ":");

        for (Map.Entry<String, Double> entry : metrics.entrySet()) {
            System.out.println("code: " + entry.getKey() + " value: " + entry.getValue());
        }
    }

    private void calculateMetrics() {

        calculatePrecision();
        calculateRecall();
        calculateAccuracy();
        calculateF1();
    }

    private void calculatePrecision() {

        Map<String, Integer> true_positive_counts = confusion_matrix.getTruePositiveCounts();
        Map<String, Integer> false_positive_counts = confusion_matrix.getFalsePositiveCounts();

        for (String code : confusion_matrix.getClassificationCounts().keySet()) {

            precision.put(code, calculatePrecision(true_positive_counts.get(code), false_positive_counts.get(code)));
        }
    }

    private void calculateRecall() {

        Map<String, Integer> true_positive_counts = confusion_matrix.getTruePositiveCounts();
        Map<String, Integer> false_negative_counts = confusion_matrix.getFalseNegativeCounts();

        for (String code : confusion_matrix.getClassificationCounts().keySet()) {

            recall.put(code, calculateRecall(true_positive_counts.get(code), false_negative_counts.get(code)));
        }
    }

    private void calculateAccuracy() {

        Map<String, Integer> true_positive_counts = confusion_matrix.getTruePositiveCounts();
        Map<String, Integer> true_negative_counts = confusion_matrix.getTrueNegativeCounts();
        Map<String, Integer> total_counts = confusion_matrix.getClassificationCounts();

        for (String code : confusion_matrix.getClassificationCounts().keySet()) {

            accuracy.put(code, calculateAccuracy(true_positive_counts.get(code), true_negative_counts.get(code), confusion_matrix.getNumberOfClassifications()));
        }
    }

    private void calculateF1() {

        Map<String, Integer> true_positive_counts = confusion_matrix.getTruePositiveCounts();
        Map<String, Integer> false_positive_counts = confusion_matrix.getFalsePositiveCounts();
        Map<String, Integer> false_negative_counts = confusion_matrix.getFalseNegativeCounts();

        for (String code : confusion_matrix.getClassificationCounts().keySet()) {

            f1.put(code, calculateF1(true_positive_counts.get(code), false_positive_counts.get(code), false_negative_counts.get(code)));
        }
    }

    private double calculatePrecision(int true_positives, int false_positives) {

        // Interpret precision as 1 if there are no positives.

        int total_positives = true_positives + false_positives;
        return total_positives > 0 ? (double) true_positives / total_positives : 1.0;
    }

    private double calculateRecall(int true_positives, int false_negatives) {

        // Interpret recall as 1 if there are no cases.

        int total_cases = true_positives + false_negatives;
        return total_cases > 0 ? (double) true_positives / total_cases : 1.0;
    }

    private double calculateAccuracy(int true_positives, int true_negatives, int total) {

        return (double) (true_positives + true_negatives) / total;
    }

    private double calculateF1(int true_positives, int false_positives, int false_negatives) {

        // Interpret F1 as 1 if there are no cases.

        int denominator = true_positives * 2 + false_positives + false_negatives;
        return denominator > 0 ? (double) (true_positives * 2) / denominator : 1.0;
    }
}
