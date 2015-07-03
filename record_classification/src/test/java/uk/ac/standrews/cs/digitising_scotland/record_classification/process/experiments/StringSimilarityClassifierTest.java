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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.experiments;

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.StringSimilarityClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.ConsistentCodingCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.Context;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.AddTrainingAndEvaluationRecordsByRatio;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.EvaluateClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.TrainClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.StringSimilarityMetric;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class StringSimilarityClassifierTest extends AbstractClassificationTest {

    private List<ConfusionMatrix> matrices;
    private List<ClassificationMetrics> metrics;

    @Before
    public void setup() throws Exception {

        final InputStreamReader occupation_data_path = getInputStreamReaderForResource(AbstractClassificationTest.class, GOLD_STANDARD_DATA_FILE_NAME);
        //        final InputStreamReader occupation_data_path = new InputStreamReader( new FileInputStream("/Users/graham/Desktop/cambridge.csv"));
        final Bucket gold_standard = new Bucket(occupation_data_path);

        final Context context = new Context();
        context.setClassifier(new StringSimilarityClassifier(StringSimilarityMetric.JARO_WINKLER));

        final ClassificationProcess process = new ClassificationProcess(context);
        process.addStep(new AddTrainingAndEvaluationRecordsByRatio(gold_standard, 0.8, ConsistentCodingCleaner.CORRECT));
        process.addStep(new TrainClassifier());
        process.addStep(new EvaluateClassifier(InfoLevel.NONE));
        process.call();

        final List<ClassificationProcess> repeat = Collections.singletonList(process);

        metrics = new ArrayList<>();
        matrices = new ArrayList<>();
        for (ClassificationProcess classificationProcess : repeat) {
            metrics.add(classificationProcess.getContext().getClassificationMetrics());
            matrices.add(classificationProcess.getContext().getConfusionMatrix());
        }
    }

    @Test
    public void checkNoExceptions() throws Exception {

    }
}
