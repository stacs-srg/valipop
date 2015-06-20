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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process;

import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.single_classifier.*;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class ExactMatchClassifierTest extends AbstractClassificationTest {

    private List<ConfusionMatrix> matrices;
    private List<ClassificationMetrics> metrics;

    @Before
    public void setup() throws Exception {

        InputStreamReader occupation_data_path = getInputStreamReaderForResource(AbstractClassificationTest.class, GOLD_STANDARD_DATA_FILE_NAME);

        ClassificationProcess classification_process = new ExactMatchClassificationProcess(occupation_data_path, 0.8, 1);
        classification_process.setInfoLevel(InfoLevel.NONE);
        classification_process.trainClassifyAndEvaluate();

        metrics = classification_process.getClassificationMetrics();
        matrices = classification_process.getConfusionMatrices();

    }

    @Test
    public void checkPrecision() throws Exception {

        // Precision must be 100% since every decision must be correct.
        ClassificationMetrics metrics_from_first_run = metrics.get(0);

        assertEquals(1.0, metrics_from_first_run.getMacroAveragePrecision(), DELTA);
        assertEquals(1.0, metrics_from_first_run.getMicroAveragePrecision(), DELTA);
    }

    @Test
    public void checkFalsePositives() throws Exception {

        // Exact match never makes an incorrect classification decision.
        for (ConfusionMatrix matrix : matrices) {
            assertEquals(0, matrix.getNumberOfFalsePositives());
        }
    }

    @Test
    public void checkTrueNegatives() throws Exception {

        // Every class but one accumulates a true negative on each classification.
        for (ConfusionMatrix matrix : matrices) {
            assertEquals((matrix.getNumberOfClasses() - 1) * matrix.getNumberOfClassifications(), matrix.getNumberOfTrueNegatives());
        }
    }

    @Test
    public void checkTruePositivesAndFalseNegatives() throws Exception {

        // Every decision by exact match must be correct (true positive) or absent (false negative).
        for (ConfusionMatrix matrix : matrices) {
            assertEquals(matrix.getNumberOfClassifications(), matrix.getNumberOfTruePositives() + matrix.getNumberOfFalseNegatives());
        }
    }
}
