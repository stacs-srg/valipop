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
package uk.ac.standrews.cs.digitising_scotland.record_classification.analysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.util.tools.Logging;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.*;

import static uk.ac.standrews.cs.util.tools.Formatting.printMetric;
import static uk.ac.standrews.cs.util.tools.Logging.output;

/**
 * Collection of metrics measuring the effectiveness of classification.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 * @see {@code http://nlp.stanford.edu/IR-book/html/htmledition/evaluation-of-text-classification-1.html#17469}
 */
public class ClassificationMetrics implements Serializable {

    /**
     * Labels for values exported by #getValues().
     */
    public static final List<String> DATASET_LABELS = Arrays.asList("macro-precision", "macro-recall", "macro-F1", "micro-precision/recall");

    private static final long serialVersionUID = -214187549269797059L;

    private final ConfusionMatrix confusion_matrix;

    /**
     * Instantiates a new classification metrics from the given confusion matrix.
     *
     * @param confusion_matrix the matrix from which to instantiate a new classification metrics
     */
    public ClassificationMetrics(ConfusionMatrix confusion_matrix) {

        this.confusion_matrix = confusion_matrix;
    }

    public List<String> getValues() {

        // Ignore accuracy since it's misleading in this domain: always high due to many true negatives.

        final String macro_precision = String.valueOf(getMacroAveragePrecision());
        final String macro_recall = String.valueOf(getMacroAverageRecall());
        final String macro_f1 = String.valueOf(getMacroAverageF1());
        final String micro_precision = String.valueOf(getMicroAveragePrecision());

        // Wrapped for mutability.
        return new ArrayList<>(Arrays.asList(macro_precision, macro_recall, macro_f1, micro_precision));
    }

    /**
     * Returns a map from classification class to precision.
     *
     * @return the map
     */
    public Map<String, Double> getPerClassPrecision() {

        final Map<String, Double> precision_map = new HashMap<>();

        Map<String, AtomicInteger> true_positive_counts = confusion_matrix.getTruePositiveCounts();
        Map<String, AtomicInteger> false_positive_counts = confusion_matrix.getFalsePositiveCounts();

        for (String code : getCodes()) {

            precision_map.put(code, calculatePrecision(true_positive_counts.get(code).get(), false_positive_counts.get(code).get()));
        }

        return precision_map;
    }

    /**
     * Returns a map from classification class to recall.
     *
     * @return the map
     */
    public Map<String, Double> getPerClassRecall() {

        final Map<String, Double> recall_map = new HashMap<>();

        Map<String, AtomicInteger> true_positive_counts = confusion_matrix.getTruePositiveCounts();
        Map<String, AtomicInteger> false_negative_counts = confusion_matrix.getFalseNegativeCounts();

        for (String code : getCodes()) {

            recall_map.put(code, calculateRecall(true_positive_counts.get(code).get(), false_negative_counts.get(code).get()));
        }

        return recall_map;
    }

    /**
     * Returns a map from classification class to accuracy.
     *
     * @return the map
     */
    public Map<String, Double> getPerClassAccuracy() {

        final Map<String, Double> accuracy_map = new HashMap<>();

        Map<String, AtomicInteger> true_positive_counts = confusion_matrix.getTruePositiveCounts();
        Map<String, AtomicInteger> true_negative_counts = confusion_matrix.getTrueNegativeCounts();
        Map<String, AtomicInteger> false_positive_counts = confusion_matrix.getFalsePositiveCounts();
        Map<String, AtomicInteger> false_negative_counts = confusion_matrix.getFalseNegativeCounts();

        for (String code : getCodes()) {

            accuracy_map.put(code, calculateAccuracy(true_positive_counts.get(code).get(), true_negative_counts.get(code).get(), false_positive_counts.get(code).get(), false_negative_counts.get(code).get()));
        }

        return accuracy_map;
    }

    /**
     * Returns a map from classification class to F1 measure.
     *
     * @return the map
     */
    public Map<String, Double> getPerClassF1() {

        final Map<String, Double> f1_map = new HashMap<>();

        Map<String, AtomicInteger> true_positive_counts = confusion_matrix.getTruePositiveCounts();
        Map<String, AtomicInteger> false_positive_counts = confusion_matrix.getFalsePositiveCounts();
        Map<String, AtomicInteger> false_negative_counts = confusion_matrix.getFalseNegativeCounts();

        for (String code : getCodes()) {

            f1_map.put(code, calculateF1(true_positive_counts.get(code).get(), false_positive_counts.get(code).get(), false_negative_counts.get(code).get()));
        }

        return f1_map;
    }

    /**
     * Returns the macro-averaged precision over all classes.
     *
     * @return the macro-averaged precision
     */
    public double getMacroAveragePrecision() {

        return getMacroAverage(getPerClassPrecision());
    }

    /**
     * Returns the macro-averaged recall over all classes.
     *
     * @return the macro-averaged recall
     */
    public double getMacroAverageRecall() {

        return getMacroAverage(getPerClassRecall());
    }

    /**
     * Returns the macro-averaged accuracy over all classes.
     *
     * @return the macro-averaged accuracy
     */
    public double getMacroAverageAccuracy() {

        return getMacroAverage(getPerClassAccuracy());
    }

    /**
     * Returns the macro-averaged F1 measure over all classes.
     *
     * @return the macro-averaged F1 measure
     */
    public double getMacroAverageF1() {

        return getMacroAverage(getPerClassF1());
    }

    /**
     * Returns the micro-averaged precision over all classes.
     *
     * @return the micro-averaged precision
     */
    public double getMicroAveragePrecision() {

        // Micro average is total TP / (total TP + total FP).
        int total_true_positives = confusion_matrix.getNumberOfTruePositives();
        int total_false_positives = confusion_matrix.getNumberOfFalsePositives();

        return calculatePrecision(total_true_positives, total_false_positives);
    }

    /**
     * Returns the micro-averaged recall over all classes.
     *
     * @return the micro-averaged recall
     */
    public double getMicroAverageRecall() {

        // Micro average is total TP / (total TP + total FN).
        int total_true_positives = confusion_matrix.getNumberOfTruePositives();
        int total_false_negatives = confusion_matrix.getNumberOfFalseNegatives();

        return calculateRecall(total_true_positives, total_false_negatives);
    }

    /**
     * Returns the micro-averaged accuracy over all classes.
     *
     * @return the micro-averaged accuracy
     */
    public double getMicroAverageAccuracy() {

        // Micro average is (total TP + total TN) / (total TP + total TN + total FP + total FN).
        int total_true_positives = confusion_matrix.getNumberOfTruePositives();
        int total_true_negatives = confusion_matrix.getNumberOfTrueNegatives();
        int total_false_positives = confusion_matrix.getNumberOfFalsePositives();
        int total_false_negatives = confusion_matrix.getNumberOfFalseNegatives();

        return calculateAccuracy(total_true_positives, total_true_negatives, total_false_positives, total_false_negatives);
    }

    /**
     * Returns the micro-averaged F1 measure over all classes.
     *
     * @return the micro-averaged F1 measure
     */
    public double getMicroAverageF1() {

        // Micro average is (2 * total TP) / (2 * total TP + total FP + total FN).
        int total_true_positives = confusion_matrix.getNumberOfTruePositives();
        int total_false_positives = confusion_matrix.getNumberOfFalsePositives();
        int total_false_negatives = confusion_matrix.getNumberOfFalseNegatives();

        return calculateF1(total_true_positives, total_false_positives, total_false_negatives);
    }

    /**
     * Prints out the metrics.
     */
    public void printMetrics() {

        printMetric(InfoLevel.VERBOSE, "total TPs", confusion_matrix.getNumberOfTruePositives());
        printMetric(InfoLevel.VERBOSE, "total FPs", confusion_matrix.getNumberOfFalsePositives());
        printMetric(InfoLevel.VERBOSE, "total TNs", confusion_matrix.getNumberOfTrueNegatives());
        printMetric(InfoLevel.VERBOSE, "total FNs", confusion_matrix.getNumberOfFalseNegatives());
        output(InfoLevel.VERBOSE, "");

        printMetrics(InfoLevel.VERBOSE, "precision", getPerClassPrecision());
        printMetrics(InfoLevel.VERBOSE, "recall", getPerClassRecall());
        printMetrics(InfoLevel.VERBOSE, "accuracy", getPerClassAccuracy());
        printMetrics(InfoLevel.VERBOSE, "F1", getPerClassF1());
        output(InfoLevel.VERBOSE, "");

        printMetric(InfoLevel.LONG_SUMMARY, "macro-average precision        ", getMacroAveragePrecision());
        printMetric(InfoLevel.LONG_SUMMARY, "macro-average recall           ", getMacroAverageRecall());
        printMetric(InfoLevel.LONG_SUMMARY, "macro-average F1               ", getMacroAverageF1());
        printMetric(InfoLevel.LONG_SUMMARY, "micro-average precision/recall ", getMicroAveragePrecision());
    }

    private double getMacroAverage(Map<String, Double> values) {

        // Macro average is mean of the individual per-class values.
        double total = 0;
        int count = 0;

        for (Map.Entry<String, Double> entry : values.entrySet()) {

            String code = entry.getKey();
            Double value = entry.getValue();

            if (!value.isNaN() && !code.equals(Classification.UNCLASSIFIED.getCode())) {
                total += value;
                count++;
            }
        }

        return total / count;
    }

    private Set<String> getCodes() {

        return confusion_matrix.getClassificationCounts().keySet();
    }

    private double calculatePrecision(int true_positives, int false_positives) {

        // Precision is TP / (TP + FP).

        return (double) true_positives / (true_positives + false_positives);
    }

    private double calculateRecall(int true_positives, int false_negatives) {

        return (double) true_positives / (true_positives + false_negatives);
    }

    private double calculateAccuracy(int true_positives, int true_negatives, int false_positives, int false_negatives) {

        int total_cases = true_positives + true_negatives + false_positives + false_negatives;
        return (double) (true_positives + true_negatives) / total_cases;
    }

    private double calculateF1(int true_positives, int false_positives, int false_negatives) {

        return (double) (true_positives * 2) / (true_positives * 2 + false_positives + false_negatives);
    }

    private void printMetrics(InfoLevel info_level, String label, Map<String, Double> metrics) {

        Logging.output(info_level, "\n" + label + ":");

        for (Map.Entry<String, Double> entry : metrics.entrySet()) {
            printMetric(info_level, entry.getKey(), entry.getValue());
        }
    }
}
