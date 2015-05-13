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

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.tools.configuration.MachineLearningConfiguration;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.ClassifyWithExistingModels;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassifyToICDWithExistingModelsTest extends AbstractTest {

    private static final String expectedModelLocation = "/Models";
    Bucket allRecords;

    @Before
    public void setUp() throws IOException, CodeNotValidException, ClassNotFoundException, InputFormatException {

        // TODO code dictionary doesn't appear to affect test results - used only for training?
        String code_dictionary_file_path = getResourceFilePath(getClass(), "ICD_code_dictionary.txt");
        String test_data_file_path = getResourceFilePath(getClass(), "classify_with_existing_models_test_data.tsv");

        // TODO resolve problem with incompatible serialised class versions, then tidy model location
        String model_directory_path = getResourceFilePath(getClass(), expectedModelLocation);

        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", code_dictionary_file_path);

        String[] args = {test_data_file_path, model_directory_path, "true"};

        allRecords = new ClassifyWithExistingModels().run(args);
    }

    @Test
    public void test() {

        assertEquals(8, allRecords.size());

        Set<Classification> classifications = allRecords.getRecord(46999).getClassifications();
        assertEquals(3, classifications.size());

        Set<String> codes_in_map = getCodeStrings(classifications);

        assertTrue(codes_in_map.contains("I340"));
        assertTrue(codes_in_map.contains("I48"));
        assertTrue(codes_in_map.contains("I515"));
    }

    @Test
    public void test2() {

        final Set<Classification> sterosisSet = allRecords.getRecord(46999).getListOfClassifications().get("mitral sterosis");
        assertEquals(1, sterosisSet.size());
        assertTrue(sterosisSet.iterator().next().getConfidence() < 1);

        final Set<Classification> myocardialSet = allRecords.getRecord(46999).getListOfClassifications().get("myocardial degeneration");
        assertEquals(1, myocardialSet.size());
    }

    @Test
    public void test3() {

        Set<Classification> classifications = allRecords.getRecord(72408).getClassifications();
        assertEquals(1, classifications.size());

        Set<String> codes_in_map = getCodeStrings(classifications);
        assertTrue(codes_in_map.contains("I340"));
    }

    @Test
    public void test4() {

        Set<Classification> classifications = allRecords.getRecord(6804).getClassifications();
        assertEquals(2, classifications.size());

        Set<String> codes_in_map = getCodeStrings(classifications);
        assertTrue(codes_in_map.contains("I219") || codes_in_map.contains("I639"));
        assertTrue(codes_in_map.contains("I515"));
    }

    @Test
    public void test5() {

        Set<Classification> classifications = allRecords.getRecord(43454).getClassifications();
        assertEquals(1, classifications.size());

        Set<String> codes_in_map = getCodeStrings(classifications);
        assertTrue(codes_in_map.contains("I219") || codes_in_map.contains("I639"));
    }

    @Test
    public void test6() {

        Set<Classification> classifications = allRecords.getRecord(6809).getClassifications();
        assertEquals(2, classifications.size());

        Set<String> codes_in_map = getCodeStrings(classifications);
        assertTrue(codes_in_map.contains("I219") || codes_in_map.contains("I639"));
        assertTrue(codes_in_map.contains("I515"));
    }

    @Test
    public void test7() {

        Set<Classification> classifications = allRecords.getRecord(9999).getClassifications();
        assertEquals(0, classifications.size());

        Set<String> codes_in_map = getCodeStrings(classifications);
        assertTrue(codes_in_map.isEmpty());
    }

    @Test
    public void test8() {

        Set<Classification> classifications = allRecords.getRecord(1234).getClassifications();
        assertEquals(3, classifications.size());

        Set<String> codes_in_map = getCodeStrings(classifications);
        assertTrue(codes_in_map.contains("I501"));
        assertTrue(codes_in_map.contains("I340"));
        assertTrue(codes_in_map.contains("I38"));

        final Set<Classification> failureSet = allRecords.getRecord(1234).getListOfClassifications().get("failure of the right ventricular");
        assertEquals(1, failureSet.size());
    }

    private static Set<String> getCodeStrings(Set<Classification> classifications) {

        Set<String> code_strings = new HashSet<>();

        for (Classification classification : classifications) {
            code_strings.add(classification.getCode().getCodeAsString());
        }
        return code_strings;
    }
}
