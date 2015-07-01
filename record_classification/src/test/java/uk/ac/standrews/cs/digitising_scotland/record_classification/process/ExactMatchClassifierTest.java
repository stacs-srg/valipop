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
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.*;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class ExactMatchClassifierTest extends AbstractClassificationTest {

    private List<ConfusionMatrix> matrices;
    private List<ClassificationMetrics> metrics;

    @Before
    public void setup() throws Exception {

        final InputStreamReader occupation_data_path = getInputStreamReaderForResource(AbstractClassificationTest.class, GOLD_STANDARD_DATA_FILE_NAME);

        final Context context = new Context();
        context.setClassifier(new ExactMatchClassifier());
        context.setGoldStandard(new Bucket(occupation_data_path));

        final ClassificationProcess process = new ClassificationProcess(context);
        process.addStep(new CleanGoldStandardRecords(ConsistentCodingCleaner.CORRECT));
        process.addStep(new SetTrainingRecordsByRatio(0.8));
        process.addStep(new TrainClassifier());
        process.addStep(new EvaluateClassifier(InfoLevel.NONE));

        final List<ClassificationProcess> repeat = process.repeat(1);

        metrics = new ArrayList<>();
        matrices = new ArrayList<>();
        for (ClassificationProcess classificationProcess : repeat) {
            metrics.add(classificationProcess.getContext().getClassificationMetrics());
            matrices.add(classificationProcess.getContext().getConfusionMatrix());
        }
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
