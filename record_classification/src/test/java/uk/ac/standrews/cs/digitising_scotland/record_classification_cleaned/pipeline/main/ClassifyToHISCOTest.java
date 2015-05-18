/*
 * Copyright 2014 Digitising Scotland project:
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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main.TrainClassifyOneFile;
import uk.ac.standrews.cs.digitising_scotland.record_classification.tools.configuration.MachineLearningConfiguration;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.ExactMatchClassificationProcess;

public class ClassifyToHISCOTest extends AbstractTest {

    public static final String CODE_DICTIONARY_FILE_NAME = "/HiscoTitles.txt";
    public static final String OCCUPATION_DATA_FILE_NAME = "/occupationTrainingTest.txt";
    public static final String CODE_DICTIONARY_PROPERTY_NAME = "codeDictionaryFile";

    @Test
    public void testExactMatchHisco() throws Exception {

        String code_dictionary_path = getResourceFilePath(getClass(), CODE_DICTIONARY_FILE_NAME);
        String occupation_data_path = getResourceFilePath(getClass(), OCCUPATION_DATA_FILE_NAME);

        MachineLearningConfiguration.getDefaultProperties().setProperty(CODE_DICTIONARY_PROPERTY_NAME, code_dictionary_path);

        String[] args = {occupation_data_path, "", "0.8", "0"};

        ClassificationProcess classification_process = new ExactMatchClassificationProcess(args);

        Bucket classified = classification_process.performClassification();
//        classified.dumpState();
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
