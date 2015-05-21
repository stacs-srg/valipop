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

    public void checkMetricValues(MetricChoice choice, ExpectedMetricValue... values) throws UnclassifiedGoldStandardRecordException, UnknownDataException, InvalidCodeException, InconsistentCodingException {

        initFullRecords();
        initMatrix();

        Map<String, Double> metric = choice.getMetric(new ClassificationMetrics(matrix));

        assertEquals(getNumberOfCodes(), metric.size());

        for (ExpectedMetricValue value : values) {
            assertEquals(value.value, metric.get(value.code), DELTA);
        }
    }

    @Test
    public void perClassPrecisionCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        checkMetricValues(new MetricChoice() {
                              @Override
                              public Map<String, Double> getMetric(ClassificationMetrics metrics) {
                                  return metrics.getPerClassPrecision();
                              }
                          },
                new ExpectedMetricValue(0.5, "fish"),      // TP 1, FP 1
                new ExpectedMetricValue(0.5, "mammal"),    // TP 1, FP 1
                new ExpectedMetricValue(1.0, "bird"),      // TP 2, FP 0
                new ExpectedMetricValue(1.0, "mythical")); // TP 0, FP 0
    }

    @Test
    public void perClassRecallCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        checkMetricValues(new MetricChoice() {
                              @Override
                              public Map<String, Double> getMetric(ClassificationMetrics metrics) {
                                  return metrics.getPerClassRecall();
                              }
                          },
                new ExpectedMetricValue(1.0, "fish"),        // TP 1, FN 0
                new ExpectedMetricValue(0.5, "mammal"),      // TP 1, FN 1
                new ExpectedMetricValue(2.0 / 3.0, "bird"),  // TP 2, FN 1
                new ExpectedMetricValue(1.0, "mythical"));   // TP 0, FN 0
    }

    @Test
    public void perClassAccuracyCalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        checkMetricValues(new MetricChoice() {
                              @Override
                              public Map<String, Double> getMetric(ClassificationMetrics metrics) {
                                  return metrics.getPerClassAccuracy();
                              }
                          },
                new ExpectedMetricValue(5.0 / 6.0, "fish"),     // TP 1, TN 4, Total 6
                new ExpectedMetricValue(4.0 / 6.0, "mammal"),   // TP 1, TN 3, Total 6
                new ExpectedMetricValue(5.0 / 6.0, "bird"),     // TP 2, TN 3, Total 6
                new ExpectedMetricValue(1.0, "mythical"));      // TP 0, TN 6, Total 6
    }

    @Test
    public void perClassF1CalculatedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        checkMetricValues(new MetricChoice() {
                              @Override
                              public Map<String, Double> getMetric(ClassificationMetrics metrics) {
                                  return metrics.getPerClassF1();
                              }
                          },
                new ExpectedMetricValue(2.0 / 3.0, "fish"),     // TP 1, FP 1, FN 0
                new ExpectedMetricValue(2.0 / 4.0, "mammal"),   // TP 1, FP 1, FN 1
                new ExpectedMetricValue(4.0 / 5.0, "bird"),     // TP 2, FP 0, FN 1
                new ExpectedMetricValue(1.0, "mythical"));      // TP 0, FP 0, FN 0
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
