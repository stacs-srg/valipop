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
package old.record_classification_old.pipeline.main.working;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.AbstractClassificationTest;
import old.record_classification_old.datastructures.bucket.Bucket;
import old.record_classification_old.pipeline.main.TrainClassifyOneFile;
import old.record_classification_old.tools.configuration.MachineLearningConfiguration;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.AbstractClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ExactMatchClassificationProcess;

@Ignore
public class ClassifyToHISCOTest extends AbstractClassificationTest {

    public static final String CODE_DICTIONARY_FILE_NAME = "/HiscoTitles.txt";
    public static final String OCCUPATION_DATA_FILE_NAME = "/occupationTrainingTest.txt";
    public static final String CODE_DICTIONARY_PROPERTY_NAME = "codeDictionaryFile";

    @Test
    public void testExactMatchHisco() throws Exception {

        String occupation_data_path = getResourceFilePath2(AbstractClassificationTest.class, GOLD_STANDARD_DATA_FILE_NAME);

        String[] args = {occupation_data_path, "0.8"};

        AbstractClassificationProcess classification_process = new ExactMatchClassificationProcess(args);

        classification_process.repeatedlyTrainClassifyAndEvaluate();

        classification_process.performClassification();
    }

    @Test
    public void testPipelineHisco() throws Exception {

        String code_dictionary_path = getResourceFilePath(getClass(), CODE_DICTIONARY_FILE_NAME);
        MachineLearningConfiguration.getDefaultProperties().setProperty(CODE_DICTIONARY_PROPERTY_NAME, code_dictionary_path);
        TrainClassifyOneFile trainer = new TrainClassifyOneFile();
        String training = getResourceFilePath(getClass(), OCCUPATION_DATA_FILE_NAME);

        String[] args = {training, "", "0.8", "0"};
        Bucket classified = trainer.run(args);
        Assert.assertEquals(20, classified.size(), 10);
    }
}
