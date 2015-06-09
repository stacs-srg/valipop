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
package old.record_classification_old.pipeline.main;

import org.junit.Ignore;
import org.junit.Test;
import old.record_classification_old.datastructures.code.CodeNotValidException;
import old.record_classification_old.tools.configuration.MachineLearningConfiguration;

@Ignore
public class ExperimentalMultipleClassificationTypesTest {

    @Test
    public void testPipelineHisco() throws Exception, CodeNotValidException {

        String codeDictionary = getClass().getResource("/HiscoTitles.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        ExperimentalMultipleClassificationTypes trainer = new ExperimentalMultipleClassificationTypes();
        String training = getClass().getResource("/occupationTrainingTest.txt").getFile();
        String[] args = {training, "0.8", "false", ""};
        trainer.run(args);
    }

    @Test
    public void testPipelineCod() throws Exception, CodeNotValidException {

        String codeDictionary = getClass().getResource("/pilotTestCodeDictionary.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        ExperimentalMultipleClassificationTypes trainer = new ExperimentalMultipleClassificationTypes();
        String training = getClass().getResource("/OneFileCodTestTrainingData.txt").getFile();
        String[] args = {training, "0.8", "true", ""};
        trainer.run(args);
    }
}
