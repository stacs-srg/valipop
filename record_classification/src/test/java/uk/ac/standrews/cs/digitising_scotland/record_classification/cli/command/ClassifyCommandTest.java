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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.apache.commons.csv.*;
import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.util.dataset.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.TestDataSets.*;

/**
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
@RunWith(Parameterized.class)
public class ClassifyCommandTest extends CommandTest {

    private ClassifierSupplier classifier_supplier;
    private List<TestDataSet> gold_standards;
    private List<TestDataSet> unseens;
    private CharsetSupplier charset;
    private CSVFormat format;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        final List<Object[]> parameters = new ArrayList<>();

        for (ClassifierSupplier classifier_supplier : ClassifierSupplier.values()) {
            for (CharsetSupplier charset_supplier : CharsetSupplier.values()) {
                for (CsvFormatSupplier format_supplier : CsvFormatSupplier.values()) {
                    parameters.add(new Object[]{classifier_supplier, CASE_2_TRAINING, CASE_2_EVALUATION, charset_supplier, format_supplier.get()});
                    parameters.add(new Object[]{classifier_supplier, CASE_4_TRAINING, CASE_4_EVALUATION, charset_supplier, format_supplier.get()});
                }
            }
        }
        return parameters;
    }

    public ClassifyCommandTest(ClassifierSupplier classifier_supplier, List<TestDataSet> gold_standards, List<TestDataSet> unseens, CharsetSupplier charset, CSVFormat format) throws IOException {

        this.classifier_supplier = classifier_supplier;
        this.gold_standards = gold_standards;
        this.unseens = unseens;
        this.charset = charset;
        this.format = format;
    }

    @Test
    public void testClassification() throws Exception {


        initForcefully();
        setVerbosity(LogLevelSupplier.OFF);
        setClassifier(classifier_supplier);
        loadGoldStandards();
        loadUnseens();
        clean();
        train();
        final Path classified_output = classify();

        checkClassification(classified_output);
    }

    private void setVerbosity(final LogLevelSupplier supplier) { new SetCommand.Builder().verbosity(supplier).run(); }

    private void setClassifier(ClassifierSupplier classifier_supplier) { new SetCommand.Builder().classifier(classifier_supplier).run(); }

    private void checkClassification(Path classified_file) throws Exception {

        assertFileExists(classified_file);
        assertSameNumberOfRecords(classified_file, unseens);
        assertRecordsContainExpectedContent(classified_file);
        assertRecordsConsistentlyClassified(classified_file);
    }

    private void assertFileExists(Path path) {

        assertTrue(Files.exists(path));
    }

    private void assertSameNumberOfRecords(Path csv_file_1, List<TestDataSet> records) throws IOException {

        final long expected_count = records.stream().mapToLong(TestDataSet::getRecordsCount).sum();
        final DataSet data_set_1 = new DataSet(csv_file_1);
        assertEquals(expected_count, data_set_1.getRecords().size());
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

        if (classifier_supplier.isEnsemble()) {
            assertRecordContainsEnsembleDetail(record);
        }
    }

    private void assertRecordsConsistentlyClassified(Path classified_csv_file) throws IOException {

        final Bucket bucket = new Bucket();

        try (final BufferedReader in = Files.newBufferedReader(classified_csv_file, Configuration.RESOURCE_CHARSET)) {
            final CSVParser parser = Configuration.RECORD_CSV_FORMAT.parse(in);
            StreamSupport.stream(parser.spliterator(), false).map(Configuration::toRecord).forEach(bucket::add);
        }

        assertTrue(new ConsistentCodingChecker().test(Collections.singletonList(bucket)));
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

        return record.get(3);
    }

    private String getConfidence(List<String> record) {

        return record.size() > 4 ? record.get(4) : null;
    }

    private String getEnsembleDetail(List<String> record) {

        return record.size() > 5 ? record.get(5) : null;
    }

    private Path classify() throws IOException {

        final Path output_path = temp.newFile().toPath();
        new ClassifyCommand.Builder().output(output_path).run();
        return output_path;
    }

    private void train() {new TrainCommand.Builder().run();}

    private void clean() {new CleanCommand.Builder().cleaners(CleanerSupplier.COMBINED).run();}

    private void loadGoldStandards() {

        gold_standards.forEach(this::loadGoldStandard);
    }

    private void loadUnseens() {

        unseens.stream().forEach(this::loadUnseen);
    }

    private void loadGoldStandard(final TestDataSet records) {

        final LoadRecordsCommand.Builder builder = new LoadGoldStandardRecordsCommand.Builder().trainingRatio(1.0).classColumnIndex(records.class_column_index);
        load(builder, records);
    }

    private void loadUnseen(final TestDataSet records) {

        load(new LoadUnseenRecordsCommand.Builder(), records);
    }

    private void load(LoadRecordsCommand.Builder builder, TestDataSet records) {

        final Path source = getTestCopy(records);
        builder.idColumnIndex(records.id_column_index).labelColumnIndex(records.label_column_index).delimiter(format.getDelimiter()).skipHeader(format.getSkipHeaderRecord()).from(source).charset(charset).run();
    }

    private Path getTestCopy(final TestDataSet records) {

        try {
            final Path destination = temp.newFile().toPath();
            records.getCopy(destination, charset.get(), format);
            return destination;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
