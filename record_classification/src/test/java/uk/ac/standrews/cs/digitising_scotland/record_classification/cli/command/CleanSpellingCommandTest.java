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

import org.junit.Test;
import org.junit.runner.*;
import org.junit.runners.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.TestDataSets.*;

/**
 * @author Masih Hajirab Derkani
 */
@RunWith(Parameterized.class)
public class CleanSpellingCommandTest extends CommandTest {

    public static final TestResource DICTIONARY = new TestResource(TestResource.class, "spelling/dictionary.txt");
    public static final List<TestDataSet> GS_WITH_MISTAKES = Arrays.asList(new TestDataSet(TestDataSet.class, "spelling/gold_standard_with_spelling_mistakes.csv"));
    public static final List<TestDataSet> GS_WITHOUT_MISTAKES = Arrays.asList(new TestDataSet(TestDataSet.class, "spelling/gold_standard_without_spelling_mistakes.csv"));
    private final List<TestDataSet> gold_standards;
    private final List<TestDataSet> unseens;
    private List<TestDataSet> expected_gold_standards;
    private List<TestDataSet> expected_unseens;
    private TestResource dictionary;
    private final CharsetSupplier dictionary_charset;
    private boolean case_sensitive;

    @Parameterized.Parameters(name = "{index}")
    public static Collection<Object[]> data() {

        final List<Object[]> parameters = new ArrayList<>();
        for (CharsetSupplier charset_supplier : CharsetSupplier.values()) {
//            parameters.add(new Object[]{
//                            CASE_2_TRAINING, CASE_2_EVALUATION, 
//                            CASE_2_TRAINING, CASE_2_EVALUATION, 
//                            DICTIONARY, charset_supplier, false});
            parameters.add(new Object[]{GS_WITH_MISTAKES, GS_WITH_MISTAKES, GS_WITHOUT_MISTAKES, GS_WITHOUT_MISTAKES, DICTIONARY, charset_supplier, false});
        }
        return parameters;
    }

    public CleanSpellingCommandTest(List<TestDataSet> gold_standards, List<TestDataSet> unseens, List<TestDataSet> expected_gold_standards, List<TestDataSet> expected_unseens, TestResource dictionary, CharsetSupplier dictionary_charset, boolean case_sensitive) throws IOException {

        this.gold_standards = gold_standards;
        this.unseens = unseens;
        this.expected_gold_standards = expected_gold_standards;
        this.expected_unseens = expected_unseens;
        this.dictionary = dictionary;
        this.dictionary_charset = dictionary_charset;
        this.case_sensitive = case_sensitive;
    }

    @Test
    public void testSpellingCorrection() throws Exception {

        initForcefully();
        setVerbosity(LogLevelSupplier.OFF);
        loadGoldStandards(gold_standards);
        loadUnseens(unseens);
        cleanSpelling();

        assertSpellingCorrected();

    }

    private void assertSpellingCorrected() throws IOException {

        final Configuration configuration = Configuration.load();
        assertGoldStandardSpellingCorrected(configuration);
        assertUnseenSpellingCorrected(configuration);
    }

    private void assertGoldStandardSpellingCorrected(final Configuration configuration) throws IOException {

        final Bucket actual = configuration.getGoldStandardRecords().get();
        final Bucket expected = expected_gold_standards.stream().map(TestDataSet::getBucket).reduce(Bucket::union).get();

        assertSpellingCorrected(actual, expected);
    }

    private void assertUnseenSpellingCorrected(final Configuration configuration) {

        final Bucket actual = configuration.getUnseenRecords().get();
        final Bucket expected = expected_unseens.stream().map(TestDataSet::getBucket).reduce(Bucket::union).get();

        assertSpellingCorrected(actual, expected);
    }

    private void assertSpellingCorrected(final Bucket actual, final Bucket expected) {

        actual.stream().forEach(actual_record -> {
            final Record expected_record = expected.findRecordById(actual_record.getId()).get();

            assertEquals(expected_record.getData(), actual_record.getData());
        });
    }

    private void cleanSpelling() throws IOException {

        final Path dictionary_copy = temp.newFile().toPath();
        dictionary.copy(dictionary_copy, dictionary_charset.get());

        new CleanSpellingCommand.Builder().accuracyThreshold(0.7f).from(dictionary_copy).caseSensitive(case_sensitive).run();
    }
}
