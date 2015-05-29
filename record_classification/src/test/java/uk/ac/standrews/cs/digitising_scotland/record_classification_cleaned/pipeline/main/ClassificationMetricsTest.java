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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.main;

import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.InconsistentCodingException;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.InvalidCodeException;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.UnclassifiedGoldStandardRecordException;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.UnknownDataException;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.ClassificationMetrics;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ClassificationMetricsTest extends AbstractMetricsTest {

    private static final double DELTA = 0.001;

    private final ExpectedMetricValue EXPECTED_PRECISION_CLASS_FISH = new ExpectedMetricValue(0.5, "fish");          // TP 1, FP 1
    private final ExpectedMetricValue EXPECTED_PRECISION_CLASS_MAMMAL = new ExpectedMetricValue(0.5, "mammal");      // TP 1, FP 1
    private final ExpectedMetricValue EXPECTED_PRECISION_CLASS_BIRD = new ExpectedMetricValue(1.0, "bird");          // TP 2, FP 0
    private final ExpectedMetricValue EXPECTED_PRECISION_CLASS_MYTHICAL = new ExpectedMetricValue(1.0, "mythical");  // TP 0, FP 0

    private final ExpectedMetricValue EXPECTED_RECALL_CLASS_FISH = new ExpectedMetricValue(1.0, "fish");             // TP 1, FN 0
    private final ExpectedMetricValue EXPECTED_RECALL_CLASS_MAMMAL = new ExpectedMetricValue(0.5, "mammal");         // TP 1, FN 1
    private final ExpectedMetricValue EXPECTED_RECALL_CLASS_BIRD = new ExpectedMetricValue(2.0 / 3.0, "bird");       // TP 2, FN 1
    private final ExpectedMetricValue EXPECTED_RECALL_CLASS_MYTHICAL = new ExpectedMetricValue(1.0, "mythical");     // TP 0, FN 0

    private final ExpectedMetricValue EXPECTED_ACCURACY_CLASS_FISH = new ExpectedMetricValue(5.0 / 6.0, "fish");     // TP 1, TN 4, Total 6
    private final ExpectedMetricValue EXPECTED_ACCURACY_CLASS_MAMMAL = new ExpectedMetricValue(4.0 / 6.0, "mammal"); // TP 1, TN 3, Total 6
    private final ExpectedMetricValue EXPECTED_ACCURACY_CLASS_BIRD = new ExpectedMetricValue(5.0 / 6.0, "bird");     // TP 2, TN 3, Total 6
    private final ExpectedMetricValue EXPECTED_ACCURACY_CLASS_MYTHICAL = new ExpectedMetricValue(1.0, "mythical");   // TP 0, TN 6, Total 6

    private final ExpectedMetricValue EXPECTED_F1_CLASS_FISH = new ExpectedMetricValue(2.0 / 3.0, "fish");           // TP 1, FP 1, FN 0
    private final ExpectedMetricValue EXPECTED_F1_CLASS_MAMMAL = new ExpectedMetricValue(2.0 / 4.0, "mammal");       // TP 1, FP 1, FN 1
    private final ExpectedMetricValue EXPECTED_F1_CLASS_BIRD = new ExpectedMetricValue(4.0 / 5.0, "bird");           // TP 2, FP 0, FN 1
    private final ExpectedMetricValue EXPECTED_F1_CLASS_MYTHICAL = new ExpectedMetricValue(1.0, "mythical");         // TP 0, FP 0, FN 0

    private final ExpectedMetricValue[] expected_per_class_precision_values = new ExpectedMetricValue[]{
            EXPECTED_PRECISION_CLASS_FISH,
            EXPECTED_PRECISION_CLASS_MAMMAL,
            EXPECTED_PRECISION_CLASS_BIRD,
            EXPECTED_PRECISION_CLASS_MYTHICAL};

    private final ExpectedMetricValue[] expected_per_class_recall_values = new ExpectedMetricValue[]{
            EXPECTED_RECALL_CLASS_FISH,
            EXPECTED_RECALL_CLASS_MAMMAL,
            EXPECTED_RECALL_CLASS_BIRD,
            EXPECTED_RECALL_CLASS_MYTHICAL};

    private final ExpectedMetricValue[] expected_per_class_accuracy_values = new ExpectedMetricValue[]{
            EXPECTED_ACCURACY_CLASS_FISH,
            EXPECTED_ACCURACY_CLASS_MAMMAL,
            EXPECTED_ACCURACY_CLASS_BIRD,
            EXPECTED_ACCURACY_CLASS_MYTHICAL};

    private final ExpectedMetricValue[] expected_per_class_f1_values = new ExpectedMetricValue[]{
            EXPECTED_F1_CLASS_FISH,
            EXPECTED_F1_CLASS_MAMMAL,
            EXPECTED_F1_CLASS_BIRD,
            EXPECTED_F1_CLASS_MYTHICAL};

    @Test
    public void perClassPrecisionCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        checkMetricValues(new MetricChoice() {
                              @Override
                              public Map<String, Double> getMetric(ClassificationMetrics metrics) {
                                  return metrics.getPerClassPrecision();
                              }
                          },
                expected_per_class_precision_values);
    }

    @Test
    public void perClassRecallCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        checkMetricValues(new MetricChoice() {
                              @Override
                              public Map<String, Double> getMetric(ClassificationMetrics metrics) {
                                  return metrics.getPerClassRecall();
                              }
                          },
                expected_per_class_recall_values);
    }

    @Test
    public void perClassAccuracyCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        checkMetricValues(new MetricChoice() {
                              @Override
                              public Map<String, Double> getMetric(ClassificationMetrics metrics) {
                                  return metrics.getPerClassAccuracy();
                              }
                          },
                expected_per_class_accuracy_values);
    }

    @Test
    public void perClassF1CalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        checkMetricValues(new MetricChoice() {
                              @Override
                              public Map<String, Double> getMetric(ClassificationMetrics metrics) {
                                  return metrics.getPerClassF1();
                              }
                          },
                expected_per_class_f1_values);
    }

    @Test
    public void macroAveragePrecisionCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Macro average is mean of the individual per-class values.

        assertEquals(average(expected_per_class_precision_values), metrics.getMacroAveragePrecision(), DELTA);
    }

    @Test
    public void microAveragePrecisionCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Micro average is total TP / (total TP + total FP).

        assertEquals((4.0 / (4.0 + 2.0)), metrics.getMicroAveragePrecision(), DELTA);
    }

    @Test
    public void macroAverageRecallCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Macro average is mean of the individual per-class values.

        assertEquals(average(expected_per_class_recall_values), metrics.getMacroAverageRecall(), DELTA);
    }

    @Test
    public void microAverageRecallCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Micro average is total TP / (total TP + total FN).

        assertEquals((4.0 / (4.0 + 2.0)), metrics.getMicroAverageRecall(), DELTA);
    }

    @Test
    public void macroAverageAccuracyCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Macro average is mean of the individual per-class values.

        assertEquals(average(expected_per_class_accuracy_values), metrics.getMacroAverageAccuracy(), DELTA);
    }

    @Test
    public void microAverageAccuracyCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Micro average is (total TP + total TN) / (total TP + total TN + total FP + total FN).

        assertEquals(((4.0 + 16.0) / (4.0 + 16.0 + 2.0 + 2.0)), metrics.getMicroAverageAccuracy(), DELTA);
    }

    @Test
    public void macroAverageF1CalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Macro average is mean of the individual per-class values.

        assertEquals(average(expected_per_class_f1_values), metrics.getMacroAverageF1(), DELTA);
    }

    @Test
    public void microAverageF1CalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Micro average is (2 * total TP) / (2 * total TP + total FP + total FN).

        assertEquals(((2 * 4.0) / (2 * 4.0 + 2.0 + 2.0)), metrics.getMicroAverageF1(), DELTA);
    }

    private double average(ExpectedMetricValue[] expected_per_class_precision_values) {

        double total = 0;
        for (ExpectedMetricValue expected_value : expected_per_class_precision_values) {
            total += expected_value.value;
        }

        return total / expected_per_class_precision_values.length;
    }

    private ClassificationMetrics getClassificationMetrics() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        initFullRecords();
        initMatrix();

        return new ClassificationMetrics(matrix);
    }

    private void checkMetricValues(MetricChoice choice, ExpectedMetricValue... values) throws UnclassifiedGoldStandardRecordException, UnknownDataException, InvalidCodeException, InconsistentCodingException {

        initFullRecords();
        initMatrix();

        Map<String, Double> metric = choice.getMetric(new ClassificationMetrics(matrix));

        assertEquals(getNumberOfCodes(), metric.size());

        for (ExpectedMetricValue value : values) {
            assertEquals(value.value, metric.get(value.code), DELTA);
        }
    }

    interface MetricChoice {
        Map<String, Double> getMetric(ClassificationMetrics metrics);
    }

    class ExpectedMetricValue {
        double value;
        String code;

        public ExpectedMetricValue(double value, String code) {
            this.value = value;
            this.code = code;
        }
    }
}
