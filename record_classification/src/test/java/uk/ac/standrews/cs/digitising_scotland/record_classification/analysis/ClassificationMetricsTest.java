/*
 * record_classification - Automatic record attribute classification.
 * Copyright © 2014-2016 Digitising Scotland project
 * (http://digitisingscotland.cs.st-andrews.ac.uk/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.analysis;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ClassificationMetricsTest extends AbstractMetricsTest {

    private final ExpectedMetricValue EXPECTED_PRECISION_CLASS_FISH = new ExpectedMetricValue(0.5, "fish");               // TP 1, FP 1
    private final ExpectedMetricValue EXPECTED_PRECISION_CLASS_MAMMAL = new ExpectedMetricValue(0.5, "mammal");           // TP 1, FP 1
    private final ExpectedMetricValue EXPECTED_PRECISION_CLASS_BIRD = new ExpectedMetricValue(1.0, "bird");               // TP 2, FP 0
    private final ExpectedMetricValue EXPECTED_PRECISION_CLASS_MYTHICAL = new ExpectedMetricValue(0.0 / 0.0, "mythical"); // TP 0, FP 0

    private final ExpectedMetricValue EXPECTED_RECALL_CLASS_FISH = new ExpectedMetricValue(1.0, "fish");                  // TP 1, FN 0
    private final ExpectedMetricValue EXPECTED_RECALL_CLASS_MAMMAL = new ExpectedMetricValue(0.5, "mammal");              // TP 1, FN 1
    private final ExpectedMetricValue EXPECTED_RECALL_CLASS_BIRD = new ExpectedMetricValue(2.0 / 3.0, "bird");            // TP 2, FN 1
    private final ExpectedMetricValue EXPECTED_RECALL_CLASS_MYTHICAL = new ExpectedMetricValue(0.0 / 1.0, "mythical");    // TP 0, FN 1

    private final ExpectedMetricValue EXPECTED_ACCURACY_CLASS_FISH = new ExpectedMetricValue(6.0 / 7.0, "fish");          // TP 1, TN 5, FP 1, FN 0
    private final ExpectedMetricValue EXPECTED_ACCURACY_CLASS_MAMMAL = new ExpectedMetricValue(5.0 / 7.0, "mammal");      // TP 1, TN 4, FP 1, FN 1
    private final ExpectedMetricValue EXPECTED_ACCURACY_CLASS_BIRD = new ExpectedMetricValue(6.0 / 7.0, "bird");          // TP 2, TN 4, FP 0, FN 1
    private final ExpectedMetricValue EXPECTED_ACCURACY_CLASS_MYTHICAL = new ExpectedMetricValue(6.0 / 7.0, "mythical");  // TP 0, TN 6, FP 0, FN 1

    private final ExpectedMetricValue EXPECTED_F1_CLASS_FISH = new ExpectedMetricValue(2.0 / 3.0, "fish");                // TP 1, FP 1, FN 0
    private final ExpectedMetricValue EXPECTED_F1_CLASS_MAMMAL = new ExpectedMetricValue(2.0 / 4.0, "mammal");            // TP 1, FP 1, FN 1
    private final ExpectedMetricValue EXPECTED_F1_CLASS_BIRD = new ExpectedMetricValue(4.0 / 5.0, "bird");                // TP 2, FP 0, FN 1
    private final ExpectedMetricValue EXPECTED_F1_CLASS_MYTHICAL = new ExpectedMetricValue(0.0 / 1.0, "mythical");        // TP 0, FP 0, FN 1

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
    public void perClassPrecisionCalculatedCorrectly() throws Exception {

        checkMetricValues(ClassificationMetrics::getPerClassPrecision, expected_per_class_precision_values);
    }

    @Test
    public void perClassRecallCalculatedCorrectly() throws Exception {

        checkMetricValues(ClassificationMetrics::getPerClassRecall, expected_per_class_recall_values);
    }

    @Test
    public void perClassAccuracyCalculatedCorrectly() throws Exception {

        checkMetricValues(ClassificationMetrics::getPerClassAccuracy, expected_per_class_accuracy_values);
    }

    @Test
    public void perClassF1CalculatedCorrectly() throws Exception {

        checkMetricValues(ClassificationMetrics::getPerClassF1, expected_per_class_f1_values);
    }

    @Test
    public void macroAveragePrecisionCalculatedCorrectly() throws Exception {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Macro average is mean of the individual per-class values.

        assertEquals(average(expected_per_class_precision_values), metrics.getMacroAveragePrecision(), DELTA);
    }

    @Test
    public void microAveragePrecisionCalculatedCorrectly() throws Exception {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Micro average is total TP / (total TP + total FP).
        // Include additional FP for unclassified decision.

        assertEquals((4.0 / (4.0 + 3.0)), metrics.getMicroAveragePrecision(), DELTA);
    }

    @Test
    public void macroAverageRecallCalculatedCorrectly() throws Exception {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Macro average is mean of the individual per-class values.

        assertEquals(average(expected_per_class_recall_values), metrics.getMacroAverageRecall(), DELTA);
    }

    @Test
    public void microAverageRecallCalculatedCorrectly() throws Exception {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Micro average is total TP / (total TP + total FN).

        assertEquals((4.0 / (4.0 + 3.0)), metrics.getMicroAverageRecall(), DELTA);
    }

    @Test
    public void macroAverageAccuracyCalculatedCorrectly() throws Exception {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Macro average is mean of the individual per-class values.

        assertEquals(average(expected_per_class_accuracy_values), metrics.getMacroAverageAccuracy(), DELTA);
    }

    @Test
    public void microAverageAccuracyCalculatedCorrectly() throws Exception {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Micro average is (total TP + total TN) / (total TP + total TN + total FP + total FN).

        assertEquals(((4.0 + 19.0) / (4.0 + 19.0 + 3.0 + 3.0)), metrics.getMicroAverageAccuracy(), DELTA);
    }

    @Test
    public void macroAverageF1CalculatedCorrectly() throws Exception {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Macro average is mean of the individual per-class values.

        assertEquals(average(expected_per_class_f1_values), metrics.getMacroAverageF1(), DELTA);
    }

    @Test
    public void microAverageF1CalculatedCorrectly() throws Exception {

        ClassificationMetrics metrics = getClassificationMetrics();

        // Micro average is (2 * total TP) / (2 * total TP + total FP + total FN).

        assertEquals(((2 * 4.0) / (2 * 4.0 + 3.0 + 3.0)), metrics.getMicroAverageF1(), DELTA);
    }

    private double average(ExpectedMetricValue[] expected_per_class_precision_values) {

        double total = 0;
        int count = 0;

        for (ExpectedMetricValue expected_value : expected_per_class_precision_values) {
            if (!expected_value.value.isNaN()) {
                total += expected_value.value;
                count++;
            }
        }

        return total / count;
    }

    private ClassificationMetrics getClassificationMetrics() throws Exception {

        initFullRecords();
        initMatrix();

        return new ClassificationMetrics(matrix);
    }

    private void checkMetricValues(MetricChoice choice, ExpectedMetricValue... values) throws Exception {

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
        Double value;
        String code;

        public ExpectedMetricValue(double value, String code) {
            this.value = value;
            this.code = code;
        }
    }
}
