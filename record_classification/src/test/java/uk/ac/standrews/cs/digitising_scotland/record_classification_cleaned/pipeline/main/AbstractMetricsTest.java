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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.main;

import org.junit.Before;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.*;
import uk.ac.standrews.cs.util.csv.CSV;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class AbstractMetricsTest {

    protected static final Record2 haddock_correct = new Record2(3, "haddock", new Classification2("fish", new TokenSet(), 1.0));
    protected static final Record2 haddock_incorrect = new Record2(3, "haddock", new Classification2("mammal", new TokenSet(), 1.0));
    protected static final Record2 osprey_incorrect = new Record2(3, "osprey", new Classification2("mammal", new TokenSet(), 1.0));
    protected static final Record2 unicorn_unclassified = new Record2(3, "unicorn", null);

    protected static final Record2 haddock_gold_standard = new Record2(3, "haddock", new Classification2("fish", new TokenSet(), 1.0));
    protected static final Record2 cow_gold_standard = new Record2(3, "cow", new Classification2("mammal", new TokenSet(), 1.0));

    protected Bucket2 classified_records;
    protected Bucket2 gold_standard_records;
    protected StrictConfusionMatrix2 matrix;

    // This is done to make sure that the name of the resource directory containing the data files is kept in sync with the class name.
    private static final String CLASS_NAME = AbstractMetricsTest.class.getSimpleName();

    private static final String CLASSIFIED_FILE_NAME = CLASS_NAME + "/classified.csv";
    private static final String GOLD_STANDARD_FILE_NAME = CLASS_NAME + "/gold_standard.csv";

    CSV classified_records_csv;
    CSV gold_standard_records_csv;

    @Before
    public void setUp() throws Exception {

        classified_records = new Bucket2();
        gold_standard_records = new Bucket2();

        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(CLASSIFIED_FILE_NAME))) {

            classified_records_csv = new CSV(reader);
        }
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(GOLD_STANDARD_FILE_NAME))) {

            gold_standard_records_csv = new CSV(reader);
        }
    }

    protected void initMatrix() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        matrix = new StrictConfusionMatrix2(classified_records, gold_standard_records);
    }

    protected void initFullRecords() throws InputFileFormatException {

        classified_records = new Bucket2(classified_records_csv);
        gold_standard_records = new Bucket2(gold_standard_records_csv);
    }

    protected int getNumberOfCodes() {

        Set<String> valid_codes = new HashSet<>();

        for (Record2 record : gold_standard_records) {

            valid_codes.add(record.getClassification().getCode());
        }
        return valid_codes.size();
    }
}
