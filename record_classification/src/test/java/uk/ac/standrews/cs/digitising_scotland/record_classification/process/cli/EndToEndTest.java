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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifiers;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.ConsistentCodingChecker;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class EndToEndTest extends EndToEndCommon {

    public EndToEndTest(Classifiers classifier_supplier, SerializationFormat serialization_format, TestInfo test_info) {

        this.classifier_supplier = classifier_supplier;
        this.serialization_format = serialization_format;
        this.gold_standard_charsets = test_info.gold_standard_charsets;
        this.unseen_data_charsets = test_info.unseen_data_charsets;
        this.gold_standard_delimiters = test_info.gold_standard_delimiters;
        this.unseen_data_delimiter = test_info.unseen_data_delimiter;

        input_gold_standard_files = getGoldStandardFiles(test_info);
        input_unseen_data_file = FileManipulation.getResourcePath(EndToEndTest.class, test_info.case_name + "/" + test_info.unseen_data_file_name);
    }

    @Test
    public void endToEndProcess() throws Exception {

        checkInitialisationLoadingAndTraining();
        checkClassification();
    }

    private void checkInitialisationLoadingAndTraining() throws Exception {

        initLoadTrain();
        assertTrainedModelFileExists();
    }

    private void assertTrainedModelFileExists() {

        assertFileExists(output_trained_model_file);
    }

    private void checkClassification() throws Exception {

        final Path classified_file = loadCleanClassify();

        assertFileExists(classified_file);
        assertSameNumberOfRecords(classified_file, input_unseen_data_file);
        assertRecordsContainExpectedContent(classified_file);
        assertRecordsConsistentlyClassified(classified_file);
    }

    private void assertFileExists(Path path) {

        assertTrue(Files.exists(path));
    }

    private void assertSameNumberOfRecords(Path csv_file_1, Path csv_file_2) throws IOException {

        final DataSet data_set_1 = new DataSet(csv_file_1);
        final DataSet data_set_2 = new DataSet(csv_file_2);

        assertEquals(data_set_1.getRecords().size(), data_set_2.getRecords().size());
    }

    private void assertRecordsContainExpectedContent(Path classified_csv_file) throws IOException {

        final DataSet data_set = new DataSet(classified_csv_file);

        for (List<String> record : data_set.getRecords()) {

            assertFirstElementIsNumber(record);

            // Exact match classifier doesn't classify unknown data.
            if (classifier_supplier != Classifiers.EXACT_MATCH) {
                assertRecordIsClassified(record);
            }
        }
    }

    private void assertRecordsConsistentlyClassified(Path classified_csv_file) throws IOException {

        final Bucket bucket = new Bucket(new DataSet(classified_csv_file));

        assertTrue(new ConsistentCodingChecker().test(bucket));
    }

    private void assertRecordIsClassified(List<String> record) {

        String classification = record.get(2);
        assertNotNull(classification);
        assertNotEquals("", classification);
        assertNotEquals("null", classification);
        assertNotEquals(Classification.UNCLASSIFIED.getCode(), classification);
    }

    private void assertFirstElementIsNumber(List<String> record) {

        //noinspection ResultOfMethodCallIgnored
        Integer.parseInt(record.get(0));
    }

    static class TestInfo {

        String case_name;
        List<String> gold_standard_file_names;
        String unseen_data_file_name;
        List<Charsets> gold_standard_charsets;
        Charsets unseen_data_charsets;
        List<String> gold_standard_delimiters;
        String unseen_data_delimiter;

        public TestInfo(String case_name, List<String> gold_standard_file_name, String unseen_data_file_name, List<Charsets> gold_standard_charsets, Charsets unseen_data_charsets, List<String> gold_standard_delimiter, String unseen_data_delimiter) {

            this.case_name = case_name;
            this.gold_standard_file_names = gold_standard_file_name;
            this.unseen_data_file_name = unseen_data_file_name;
            this.gold_standard_charsets = gold_standard_charsets;
            this.unseen_data_charsets = unseen_data_charsets;
            this.gold_standard_delimiters = gold_standard_delimiter;
            this.unseen_data_delimiter = unseen_data_delimiter;
        }

        public String toString() {
            return case_name;
        }
    }

    @Parameterized.Parameters(name = "{0}, {1}, {2}")
    public static Collection<Object[]> generateData() {

        List<Classifiers> classifiers = Arrays.asList(
                Classifiers.EXACT_MATCH,
                Classifiers.OLR,
                Classifiers.EXACT_MATCH_PLUS_VOTING_ENSEMBLE);

        List<SerializationFormat> serialization_formats = Arrays.asList(
                SerializationFormat.JSON,
                SerializationFormat.JSON_COMPRESSED,
                SerializationFormat.JAVA_SERIALIZATION);

        List<TestInfo> cases = Arrays.asList(makeCase1(), makeCase2(), makeCase3(), makeCase4());

        return allCombinations(classifiers, serialization_formats, cases);
    }

    private static TestInfo makeCase1() {

        return new TestInfo("case1",
                Arrays.asList("test_training_data.csv"), "test_evaluation_data.csv",
                Arrays.asList(Charsets.UTF_8), Charsets.UTF_8, Arrays.asList(","), ",");
    }

    private static TestInfo makeCase2() {

        return new TestInfo("case2",
                Arrays.asList("test_training_data.csv"), "test_evaluation_data.txt",
                Arrays.asList(Charsets.UTF_8), Charsets.UTF_8, Arrays.asList(","), "|");
    }

    private static TestInfo makeCase3() {

        return new TestInfo("case3",
                Arrays.asList("gold_standard1.csv", "gold_standard2.csv"), "unseen_data.csv",
                Arrays.asList(Charsets.UTF_8, Charsets.UTF_8), Charsets.UTF_8, Arrays.asList(",", ","), ",");
    }

    private static TestInfo makeCase4() {

        return new TestInfo("case4",
                Arrays.asList("gold_standard1.csv", "gold_standard2.csv"), "unseen_data.csv",
                null, null, null, null);
    }

    private List<Path> getGoldStandardFiles(TestInfo test_info) {

        List<Path> paths = new ArrayList<>();

        for (String gold_standard_file_name : test_info.gold_standard_file_names) {

            paths.add(FileManipulation.getResourcePath(EndToEndTest.class, test_info.case_name + "/" + gold_standard_file_name));
        }

        return paths;
    }

    private static Collection<Object[]> allCombinations(List<Classifiers> classifier_suppliers, List<SerializationFormat> serialization_formats, List<TestInfo> cases) {

        List<Object[]> result = new ArrayList<>();

        for (Classifiers classifier_supplier : classifier_suppliers) {
            for (SerializationFormat serialization_format : serialization_formats) {
                for (TestInfo test_case : cases) {
                    result.add(new Object[]{classifier_supplier, serialization_format, test_case});
                }
            }
        }

        return result;
    }
}
