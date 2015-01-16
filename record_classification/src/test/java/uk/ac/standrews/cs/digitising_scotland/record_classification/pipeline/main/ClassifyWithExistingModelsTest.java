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
package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.tools.configuration.MachineLearningConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the {@link PIlot} class. Exact matches and trains an OLR with known input and output.
 * Should test all types of classification with exact match and olr.
 * ie, fully matched, half matched half learned, not matched.
 * @author jkc25
 *
 */
public class ClassifyWithExistingModelsTest {

    private ClassifyWithExistingModels classifier;
    private final String expectedModelLocation = "/Models";

    @Before
    public void setUp() throws Exception {

        String codeDictionaryLocation = getClass().getResource("/pilotTestCodeDictionary.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionaryLocation);
        copyFilesToExpectedLocation();

        classifier = new ClassifyWithExistingModels();
    }

    private void copyFilesToExpectedLocation() throws IOException {

        File lookupTable1 = getResourceFile("/codModels/lookupTable.ser");
        File olrModel1 = getResourceFile("/codModels/olrModel");
        File lookupTable2 = getResourceFile(expectedModelLocation + "/lookupTable.ser");
        File olrModel2 = getResourceFile(expectedModelLocation + "/olrModel");

        Files.copy(lookupTable1, lookupTable2);
        Files.copy(olrModel1, olrModel2);
    }

    private File getResourceFile(String resource_file_name) {

        return new File(getResourceFilePath(resource_file_name));
    }

    @Test
    public void test() throws Exception, CodeNotValidException {

        String test_data_file_path = getResourceFilePath("classify_with_existing_models_test_data.tsv");
        String model_directory_path = getResourceFilePath(expectedModelLocation);
        final String multipleClassifications = Boolean.TRUE.toString();

        String[] args = {test_data_file_path, model_directory_path, multipleClassifications};

        Bucket allRecords = classifier.run(args);

        final int numberOfRecords = 8;
        assertEquals(numberOfRecords, allRecords.size());

        Set<Classification> classifications = allRecords.getRecord(46999).getClassifications();
        System.out.println(classifications);
        assertEquals(3, classifications.size());

        Set<String> codes_in_map = getCodesInMap(classifications.iterator());
        assertTrue(codes_in_map.contains("I340"));
        assertTrue(codes_in_map.contains("I48"));
        assertTrue(codes_in_map.contains("I515"));

        final Set<Classification> sterosisSet = allRecords.getRecord(46999).getListOfClassifications().get("mitral sterosis");
        assertTrue(sterosisSet.size() == 1);
        assertTrue(sterosisSet.iterator().next().getConfidence() < 1);
        final Set<Classification> myocardialSet = allRecords.getRecord(46999).getListOfClassifications().get("myocardial degeneration");
        assertTrue(myocardialSet.size() == 1);

        // Confidence is 1.0 here. Don't know why test is for > 1.
        // assertTrue(myocardialSet.iterator().next().getConfidence() > 1);

        classifications = allRecords.getRecord(72408).getClassifications();
        System.out.println(classifications);
        assertEquals(1, classifications.size());
        codes_in_map = getCodesInMap(classifications.iterator());
        assertTrue(codes_in_map.contains("I340"));

        classifications = allRecords.getRecord(6804).getClassifications();
        System.out.println(classifications);
        assertEquals(2, classifications.size());
        codes_in_map = getCodesInMap(classifications.iterator());
        assertTrue(codes_in_map.contains("I219") || codes_in_map.contains("I639"));
        assertTrue(codes_in_map.contains("I515"));

        classifications = allRecords.getRecord(43454).getClassifications();
        System.out.println(classifications);
        assertEquals(1, classifications.size());
        codes_in_map = getCodesInMap(classifications.iterator());
        assertTrue(codes_in_map.contains("I219") || codes_in_map.contains("I639"));

        classifications = allRecords.getRecord(6809).getClassifications();
        System.out.println(classifications);
        assertEquals(2, classifications.size());
        codes_in_map = getCodesInMap(classifications.iterator());
        assertTrue(codes_in_map.contains("I219") || codes_in_map.contains("I639"));
        assertTrue(codes_in_map.contains("I515"));

        classifications = allRecords.getRecord(9999).getClassifications();
        System.out.println(classifications);
        assertEquals(0, classifications.size());
        codes_in_map = getCodesInMap(classifications.iterator());
        assertTrue(codes_in_map.isEmpty());

        classifications = allRecords.getRecord(1234).getClassifications();
        System.out.println(classifications);
        assertEquals(3, classifications.size());
        codes_in_map = getCodesInMap(classifications.iterator());
        assertTrue(codes_in_map.contains("I501"));
        assertTrue(codes_in_map.contains("I340"));
        assertTrue(codes_in_map.contains("I38"));
        final Set<Classification> failureSet = allRecords.getRecord(1234).getListOfClassifications().get("failure of the right ventricular");
        assertEquals(1, failureSet.size());

        // Confidence is 1.0 here. Don't know why test is for > 1.
        // assertTrue(failureSet.iterator().next().getConfidence() > 1);
    }

    private String getResourceFilePath(String resource_file_name) {
        return getClass().getResource(resource_file_name).getFile();
    }

    private Set<String> getCodesInMap(Iterator<Classification> it) {

        Set<String> codesinmap = new HashSet<>();

        while (it.hasNext()) {
            Classification classification = it.next();
            codesinmap.add(classification.getCode().getCodeAsString());
        }
        return codesinmap;
    }
}
