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
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ClassifierSupplier;
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
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class EndToEndTest extends EndToEndCommon {

    public EndToEndTest(ClassifierSupplier classifier_supplier, SerializationFormat serialization_format, boolean use_cli, boolean include_ensemble_detail, TestInfo test_info) {

        this.classifier_supplier = classifier_supplier;
        this.serialization_format = serialization_format;
        this.use_cli = use_cli;
        this.include_ensemble_detail = include_ensemble_detail;
        this.gold_standard_charset_suppliers = test_info.gold_standard_charsets;
        this.unseen_data_charset_supplier = test_info.unseen_data_charsets;
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

        new DataSet(classified_csv_file).getRecords().forEach(this::assertRecordContainsExpectedContent);
    }

    private void assertRecordContainsExpectedContent(List<String> record) {

        assertRecordContainsId(record);

        // Exact match classifier doesn't classify unknown data.
        if (!classifier_supplier.isExactMatch()) {

            assertRecordContainsClassification(record);
            assertRecordContainsConfidence(record);
        }

        if (include_ensemble_detail && classifier_supplier.isEnsemble()) {

            assertRecordContainsEnsembleDetail(record);
        }
    }

    private void assertRecordsConsistentlyClassified(Path classified_csv_file) throws IOException {

        final Bucket bucket = new Bucket(new DataSet(classified_csv_file));

        assertTrue(new ConsistentCodingChecker().test(bucket));
    }

    private void assertRecordContainsId(List<String> record) {

        //noinspection ResultOfMethodCallIgnored
        Integer.parseInt(getId(record));
    }

    private void assertRecordContainsClassification(List<String> record) {

        String classification = getClassification(record);
        assertPresent(classification);
        assertNotEquals(Classification.UNCLASSIFIED.getCode(), classification);
    }

    private void assertPresent(String value) {

        assertNotNull(value);
        assertNotEquals("", value);
        assertNotEquals("null", value);
    }

    private void assertRecordContainsConfidence(List<String> record) {

        assertBetween(Double.parseDouble(getConfidence(record)), 0.0, 1.0);
    }

    private void assertRecordContainsEnsembleDetail(List<String> record) {

        assertPresent(getEnsembleDetail(record));
    }

    private void assertBetween(double value, double lower, double higher) {

        assertTrue(value >= lower);
        assertTrue(value <= higher);
    }

    private String getId(List<String> record) {
        return record.get(0);
    }

    private String getClassification(List<String> record) {
        return record.get(2);
    }

    private String getConfidence(List<String> record) {
        return record.size() > 3 ? record.get(3) : null;
    }

    private String getEnsembleDetail(List<String> record) {
        return record.size() > 4 ? record.get(4) : null;
    }

    static class TestInfo {

        String case_name;
        List<String> gold_standard_file_names;
        String unseen_data_file_name;
        List<CharsetSupplier> gold_standard_charsets;
        CharsetSupplier unseen_data_charsets;
        List<String> gold_standard_delimiters;
        String unseen_data_delimiter;

        public TestInfo(String case_name, List<String> gold_standard_file_name, String unseen_data_file_name, List<CharsetSupplier> gold_standard_charsets, CharsetSupplier unseen_data_charsets, List<String> gold_standard_delimiter, String unseen_data_delimiter) {

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

    @Parameterized.Parameters(name = "{0}, {1}, {2}, {3}, {4}")
    public static Collection<Object[]> generateData() {

        List<ClassifierSupplier> classifiers = asList(
                ClassifierSupplier.EXACT_MATCH,
                ClassifierSupplier.STRING_SIMILARITY_JARO_WINKLER,
                ClassifierSupplier.OLR,
                ClassifierSupplier.EXACT_MATCH_PLUS_STRING_SIMILARITY_LEVENSHTEIN,
                ClassifierSupplier.VOTING_ENSEMBLE_EXACT_OLR_SIMILARITY);

        List<SerializationFormat> serialization_formats = asList(
                SerializationFormat.JSON,
                SerializationFormat.JSON_COMPRESSED,
                SerializationFormat.JAVA_SERIALIZATION);

        List<Boolean> use_cli_options = asList(true, false);
        List<Boolean> include_ensemble_detail_options = asList(true, false);

        List<TestInfo> cases = asList(makeCase1(), makeCase2(), makeCase3(), makeCase4(), makeCase5());

        return allCombinations(classifiers, serialization_formats, use_cli_options, include_ensemble_detail_options, cases);
    }

    private static TestInfo makeCase1() {

        return new TestInfo("case1",
                singletonList("test_training_data.csv"), "test_evaluation_UTF8_unix.csv",
                singletonList(CharsetSupplier.UTF_8), CharsetSupplier.UTF_8, singletonList(","), ",");
    }

    private static TestInfo makeCase2() {

        return new TestInfo("case2",
                singletonList("test_training_UTF8_unix.csv"), "test_evaluation_UTF8_windows.txt",
                singletonList(CharsetSupplier.UTF_8), CharsetSupplier.UTF_8, singletonList(","), "|");
    }

    private static TestInfo makeCase3() {

        return new TestInfo("case3",
                asList("gold_standard1.csv", "gold_standard2.csv"), "unseen_data.csv",
                asList(CharsetSupplier.UTF_8, CharsetSupplier.UTF_8), CharsetSupplier.UTF_8, asList(",", ","), ",");
    }

    private static TestInfo makeCase4() {

        return new TestInfo("case4",
                asList("gold_standard1.csv", "gold_standard2.csv"), "unseen_data.csv",
                null, null, null, null);
    }

    private static TestInfo makeCase5() {

        return new TestInfo("case5",
                asList("test_training_ascii_unix.csv", "test_training_iso_latin1_unix.csv", "test_training_UTF16_unix.csv", "test_training_windows_windows.csv"), "test_evaluation_ascii_windows.csv",
                asList(CharsetSupplier.US_ASCII, CharsetSupplier.ISO_8859_1, CharsetSupplier.UTF_16, CharsetSupplier.UTF_8), CharsetSupplier.UTF_8, asList(",", ",", ",", ",", ","), ",");
    }

    private List<Path> getGoldStandardFiles(TestInfo test_info) {

        List<Path> paths = new ArrayList<>();

        for (String gold_standard_file_name : test_info.gold_standard_file_names) {

            paths.add(FileManipulation.getResourcePath(EndToEndTest.class, test_info.case_name + "/" + gold_standard_file_name));
        }

        return paths;
    }

    private static Collection<Object[]> allCombinations(List<ClassifierSupplier> classifier_suppliers, List<SerializationFormat> serialization_formats, List<Boolean> use_cli_options, List<Boolean> include_ensemble_detail_options, List<TestInfo> cases) {

        List<Object[]> result = new ArrayList<>();

        for (ClassifierSupplier classifier_supplier : classifier_suppliers) {
            for (SerializationFormat serialization_format : serialization_formats) {
                for (boolean use_cli : use_cli_options) {
                    for (boolean include_ensemble_detail : include_ensemble_detail_options) {
                        for (TestInfo test_case : cases) {
                            result.add(new Object[]{classifier_supplier, serialization_format, use_cli, include_ensemble_detail, test_case});
                        }
                    }
                }
            }
        }

        return result;
    }
}
