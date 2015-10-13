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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ClassifierSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.EnglishStopWordCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenList;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class EndToEndCleaningTest extends EndToEndCommon {

    static final String GOLD_STANDARD_FILE_NAME = "test_training_data.csv";
    static final String EVALUATION_FILE_NAME = "test_evaluation_data.csv";

    private static final List<String> SUFFIXES = Arrays.asList("ing", "s");
    private static final List<String> PUNCTUATION_CHARACTERS =
            Arrays.asList(".", ",","'","\"","!", "-", "+", "@", "|","<",">","%","&","*","(",")","/","\\");

    public EndToEndCleaningTest() {

        classifier_supplier = ClassifierSupplier.STRING_SIMILARITY_DICE;
        serialization_format = SerializationFormat.JSON;

        input_gold_standard_files = Arrays.asList(FileManipulation.getResourcePath(EndToEndCleaningTest.class, GOLD_STANDARD_FILE_NAME));
        training_ratios = Arrays.asList(1.0);

        input_unseen_data_file = FileManipulation.getResourcePath(EndToEndCleaningTest.class, EVALUATION_FILE_NAME);
    }

    @Test
    public void goldStandardRecordsAreCleaned() throws Exception {

        initLoadTrain();

        assertRecordsAreCleaned(loadContext().getTrainingRecords());
        assertRecordsAreCleaned(loadContext().getEvaluationRecords());
    }

    @Test
    public void dataRecordsAreCleaned() throws Exception {

        initLoadTrain();
        loadCleanClassify();

        assertRecordsAreCleaned(loadContext().getClassifiedUnseenRecords());
    }

    private void assertRecordsAreCleaned(Bucket bucket) {

        for (Record record : bucket) {
            assertClean(record);
        }
    }

    private void assertClean(Record record) {

        String data = record.getData();
        String classification_tokens = record.getClassification().getTokenList().toString();

        assertClean(data);
        assertClean(classification_tokens);
    }

    private void assertClean(String s) {

        assertNoPunctuation(s);

        for (String token : new TokenList(s)) {
            assertNotInStopWords(token);
            assertNoSuffix(token);
        }
    }

    private void assertNoPunctuation(String s) {

        for (String punctuation_character : PUNCTUATION_CHARACTERS) {
            assertFalse(s.contains(punctuation_character));
        }
    }

    private void assertNotInStopWords(String token) {

        for (Object o : EnglishStopWordCleaner.DEFAULT_STOP_WORDS) {

            String stop_word = String.valueOf((char[]) o);
            assertNotEquals(token, stop_word);
        }
    }

    private void assertNoSuffix(String token) {

        for (String suffix : SUFFIXES) {

            assertFalse(token.endsWith(suffix));
        }
    }

    private ClassificationContext loadContext() throws IOException {

        return Serialization.loadContext(temp_process_directory, process_name, serialization_format);
    }
}
