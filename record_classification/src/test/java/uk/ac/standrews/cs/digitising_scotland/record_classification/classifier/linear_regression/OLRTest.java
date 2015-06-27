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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression;

import old.record_classification_old.datastructures.bucket.Bucket;
import old.record_classification_old.tools.configuration.MachineLearningConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.AbstractClassificationTest;

@Ignore
public class OLRTest extends AbstractClassificationTest {

    @Test
    public void classifyOld() throws Exception {

        String code_dictionary_path = "/Users/graham/Documents/Code/IntelliJ/digitising_scotland/record_classification/src/test/resources/old/HiscoTitles.txt";
        String training = "/Users/graham/Documents/Code/IntelliJ/digitising_scotland/record_classification/src/test/resources/old/occupationTrainingTest.txt";

        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", code_dictionary_path);

        old.record_classification_old.pipeline.main.TrainClassifyOneFile trainer = new old.record_classification_old.pipeline.main.TrainClassifyOneFile();

        String[] args = {training, "", "0.8", "0"};
        Bucket classified = trainer.run(args);
//        Assert.assertEquals(20, classified.size(), 10);

        System.out.println("classified bucket:");
        System.out.println(classified);
    }

    @Test
    public void classifyNew() throws Exception {

        String code_dictionary_path = "/Users/graham/Documents/Code/IntelliJ/digitising_scotland/record_classification/src/test/resources/old/HiscoTitles.txt";
        String training = "/Users/graham/Documents/Code/IntelliJ/digitising_scotland/record_classification/src/test/resources/old/occupationTrainingTest.txt";

        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", code_dictionary_path);

        TrainClassifyOneFile trainer = new TrainClassifyOneFile();

//        String[] args = {training, "", "0.8", "0"};
//        Bucket classified = trainer.run(args);
//        Assert.assertEquals(20, classified.size(), 10);

//        System.out.println("classified bucket:");
//        System.out.println(classified);
    }
}
