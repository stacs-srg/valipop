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

import org.junit.Before;
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
public class EndToEndTest {

    private static final String CLASSIFIED_FILE_NAME = "classified.csv";
    private static final String GOLD_STANDARD_FILE_NAME = "test_training_data.csv";
    private static final String EVALUATION_FILE_NAME = "test_evaluation_data.csv";

    SerializationFormat serialization_format;
    String process_name;
    Classifiers classifier_supplier;
    double training_ratio;

    Path temp_process_directory;
    Path input_gold_standard_file;
    Path output_trained_model_file;

    Path input_unseen_data_file;
    Path output_classified_file;

    @Before
    public void setup() throws IOException {

        process_name = Command.PROCESS_NAME;
        training_ratio = 0.8;

        temp_process_directory = Files.createTempDirectory(process_name + "_");
        input_gold_standard_file = FileManipulation.getResourcePath(EndToEndTest.class, GOLD_STANDARD_FILE_NAME);
        output_trained_model_file = Command.getSerializedContextPath(temp_process_directory, process_name, serialization_format);

        input_unseen_data_file = FileManipulation.getResourcePath(EndToEndTest.class, EVALUATION_FILE_NAME);;
        output_classified_file = Command.getProcessWorkingDirectory(temp_process_directory, process_name).resolve(CLASSIFIED_FILE_NAME);
    }

    @Parameterized.Parameters(name = "{0}, {1}")
    public static Collection<Object[]> generateData() {

        List<Classifiers> classifiers = Arrays.asList(Classifiers.EXACT_MATCH, Classifiers.EXACT_MATCH.OLR);
        List<SerializationFormat> serialization_formats = Arrays.asList(SerializationFormat.JSON, SerializationFormat.COMPRESSED_JSON, SerializationFormat.JAVA_SERIALIZATION);

        return allCombinations(classifiers, serialization_formats);
    }

    public EndToEndTest(Classifiers classifier_supplier, SerializationFormat serialization_format) {

        this.classifier_supplier = classifier_supplier;
        this.serialization_format = serialization_format;
    }

    @Test
    public void endToEndOLRProcess() throws Exception {

        checkInitialisationLoadingAndTraining();
        checkClassification();
    }

    private static Collection<Object[]> allCombinations(List<Classifiers> classifier_suppliers, List<SerializationFormat> serialization_formats) {

        List<Object[]> result = new ArrayList<>();

        for (Classifiers classifier_supplier : classifier_suppliers) {
            for (SerializationFormat serialization_format : serialization_formats) {
                result.add(new Object[]{classifier_supplier, serialization_format});
            }
        }

        return result;
    }

    private void checkInitialisationLoadingAndTraining() throws Exception {

        initLoadTrain();
        assertTrainedModelFileExists();
    }

    private void initLoadTrain() throws Exception {

        InitLoadTrainCommand.initLoadTrain(classifier_supplier, input_gold_standard_file, training_ratio, serialization_format, process_name, temp_process_directory);
    }

    private void assertTrainedModelFileExists() {

        assertFileExists(output_trained_model_file);
    }

    private void checkClassification() throws Exception {

        final Path classified_file = classify();

        assertFileExists(classified_file);
        assertSameNumberOfRecords(classified_file, input_gold_standard_file);
        assertRecordsContainExpectedContent(classified_file);
        assertRecordsConsistentlyClassified(classified_file);
    }

    private Path classify() throws Exception {

        ClassifyCommand.classify(input_unseen_data_file, output_classified_file, serialization_format, process_name, temp_process_directory);
        return output_classified_file;
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
}
