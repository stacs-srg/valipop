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

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MultipleClassifierConfusionMatrixTest {

    private static final String CLASSIFIED_FILE_NAME = "example_classified.csv";
    private static final String GOLD_STANDARD_FILE_NAME = "example_gold_standard.csv";

    private static final double DELTA = 0.001;

    private DataSet classified_records_csv;
    private DataSet gold_standard_records_csv;

    private StrictConfusionMatrix matrix;

    @Before
    public void setUp() throws Exception {

        try (InputStreamReader reader = FileManipulation.getInputStreamReaderForResource(MultipleClassifierConfusionMatrixTest.class, CLASSIFIED_FILE_NAME)) {

            classified_records_csv = new DataSet(reader, ',');
        }
        try (InputStreamReader reader = FileManipulation.getInputStreamReaderForResource(MultipleClassifierConfusionMatrixTest.class, GOLD_STANDARD_FILE_NAME)) {

            gold_standard_records_csv = new DataSet(reader, ',');
        }
    }

    protected void initMatrix() throws Exception {

        matrix = new StrictConfusionMatrix(classified_records_csv, gold_standard_records_csv, null);
    }

    @Test
    public void perCodeClassificationsCountedCorrectly() throws Exception {

        initMatrix();

        Map<String, Integer> classification_counts = matrix.getClassificationCounts();

        assertEquals(getNumberOfCodesFromClassifier(), classification_counts.size());

        assertCount(2, "fish", classification_counts);
        assertCount(2, "mammal", classification_counts);
        assertCount(2, "bird", classification_counts);
        assertCount(0, "mythical", classification_counts);
        assertCount(1, "tasty", classification_counts);
        assertCount(2, "big", classification_counts);
        assertCount(2, "small", classification_counts);
        assertCount(1, "predator", classification_counts);
    }

    @Test
    public void truePositivesCountedCorrectly() throws Exception {

        initMatrix();

        Map<String, Integer> true_positive_counts = matrix.getTruePositiveCounts();

        assertEquals(getNumberOfCodesFromClassifier(), true_positive_counts.size());

        assertCount(1, "fish", true_positive_counts);
        assertCount(1, "mammal", true_positive_counts);
        assertCount(2, "bird", true_positive_counts);
        assertCount(0, "mythical", true_positive_counts);
        assertCount(0, "tasty", true_positive_counts);
        assertCount(1, "big", true_positive_counts);
        assertCount(1, "small", true_positive_counts);
        assertCount(1, "predator", true_positive_counts);
    }

    @Test
    public void trueNegativesCountedCorrectly() throws Exception {

        initMatrix();

        Map<String, Integer> true_negative_counts = matrix.getTrueNegativeCounts();

        assertEquals(getNumberOfCodesFromClassifier(), true_negative_counts.size());

        assertCount(5, "fish", true_negative_counts);
        assertCount(4, "mammal", true_negative_counts);
        assertCount(4, "bird", true_negative_counts);
        assertCount(6, "mythical", true_negative_counts);
        assertCount(6, "tasty", true_negative_counts);
        assertCount(3, "big", true_negative_counts);
        assertCount(3, "small", true_negative_counts);
        assertCount(6, "predator", true_negative_counts);
    }

    @Test
    public void falsePositivesCountedCorrectly() throws Exception {

        initMatrix();

        Map<String, Integer> false_positive_counts = matrix.getFalsePositiveCounts();

        assertEquals(getNumberOfCodesFromClassifier(), false_positive_counts.size());

        assertCount(1, "fish", false_positive_counts);
        assertCount(1, "mammal", false_positive_counts);
        assertCount(0, "bird", false_positive_counts);
        assertCount(0, "mythical", false_positive_counts);
        assertCount(1, "tasty", false_positive_counts);
        assertCount(1, "big", false_positive_counts);
        assertCount(1, "small", false_positive_counts);
        assertCount(0, "predator", false_positive_counts);

        assertCount(1, Classification.UNCLASSIFIED.getCode(), false_positive_counts);
    }

    @Test
    public void falseNegativesCountedCorrectly() throws Exception {

        initMatrix();

        Map<String, Integer> false_negative_counts = matrix.getFalseNegativeCounts();

        assertEquals(getNumberOfCodesFromClassifier(), false_negative_counts.size());

        assertCount(0, "fish", false_negative_counts);
        assertCount(1, "mammal", false_negative_counts);
        assertCount(1, "bird", false_negative_counts);
        assertCount(1, "mythical", false_negative_counts);
        assertCount(0, "tasty", false_negative_counts);
        assertCount(2, "big", false_negative_counts);
        assertCount(2, "small", false_negative_counts);
        assertCount(0, "predator", false_negative_counts);
    }

    private void assertCount(int count, String code, Map<String, Integer> counts) {

        if (counts.containsKey(code)) {
            assertEquals(count, (int) counts.get(code));
        }
    }

    @Test
    public void otherStatsCalculatedCorrectly() throws Exception {

        initMatrix();

        assertEquals(average(3, 1, 2, 2, 2, 2, 0), matrix.averageClassificationsPerRecord(), DELTA);
        assertEquals(average(2, 2, 2, 2, 3, 2, 2, 1), matrix.actualAverageClassificationsPerRecord(), DELTA);
        assertEquals(3.0 / 7.0, matrix.proportionOfRecordsWithCorrectNumberOfClassifications(), DELTA);
    }

    private double average(double... numbers) {

        double sum = 0.0;
        for (double n : numbers) {
            sum += n;
        }
        return sum / numbers.length;
    }

    protected int getNumberOfCodesFromClassifier() {

        Set<String> codes = new HashSet<>();

        for (List<String> record : classified_records_csv.getRecords()) {

            boolean found_a_code = false;

            for (String code : record.subList(2, record.size())) {

                if (code.length() > 0) {
                    codes.add(code);
                    found_a_code = true;
                }
            }

            if (!found_a_code) {
                codes.add(Classification.UNCLASSIFIED.getCode());
            }
        }

        return codes.size();
    }
}
