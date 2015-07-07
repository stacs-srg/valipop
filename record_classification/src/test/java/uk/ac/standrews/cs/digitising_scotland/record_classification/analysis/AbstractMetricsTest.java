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
package uk.ac.standrews.cs.digitising_scotland.record_classification.analysis;

import org.junit.Before;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class AbstractMetricsTest {

    protected static final double DELTA = 0.001;

    protected static final Record haddock_correct = new Record(1, "haddock", new Classification("fish", new TokenSet(), 1.0));
    protected static final Record haddock_incorrect = new Record(2, "haddock", new Classification("mammal", new TokenSet(), 1.0));
    protected static final Record osprey_incorrect = new Record(3, "osprey", new Classification("mammal", new TokenSet(), 1.0));
    protected static final Record unicorn_unclassified = new Record(4, "unicorn", Classification.UNCLASSIFIED);

    protected static final Record haddock_gold_standard = new Record(5, "haddock", new Classification("fish", new TokenSet(), 1.0));
    protected static final Record cow_gold_standard = new Record(6, "cow", new Classification("mammal", new TokenSet(), 1.0));

    protected Bucket classified_records;
    protected Bucket gold_standard_records;
    protected StrictConfusionMatrix matrix;

    private static final String CLASSIFIED_FILE_NAME =  "example_classified.csv";
    private static final String GOLD_STANDARD_FILE_NAME = "example_gold_standard.csv";

    DataSet classified_records_csv;
    DataSet gold_standard_records_csv;

    @Before
    public void setUp() throws Exception {

        classified_records = new Bucket();
        gold_standard_records = new Bucket();

        try (InputStreamReader reader = FileManipulation.getInputStreamReaderForResource(AbstractMetricsTest.class, CLASSIFIED_FILE_NAME)) {

            classified_records_csv = new DataSet(reader);
        }
        try (InputStreamReader reader = FileManipulation.getInputStreamReaderForResource(AbstractMetricsTest.class, GOLD_STANDARD_FILE_NAME)) {

            gold_standard_records_csv = new DataSet(reader);
        }
    }

    protected void initMatrix() throws Exception {

        matrix = new StrictConfusionMatrix(classified_records, gold_standard_records, new InconsistentCodingChecker());
    }

    protected void initFullRecords() throws InputFileFormatException {

        classified_records = new Bucket(classified_records_csv);
        gold_standard_records = new Bucket(gold_standard_records_csv);
    }

    protected int getNumberOfCodes() {

        Set<String> valid_codes = new HashSet<>();

        for (Record record : gold_standard_records) {

            valid_codes.add(record.getClassification().getCode());
        }
        valid_codes.add(Classification.UNCLASSIFIED.getCode());
        return valid_codes.size();
    }
}
