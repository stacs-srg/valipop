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
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.*;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConfusionMatrixTest {

    private static final Record2 test_record_1 = new Record2(3, "haddock", new Classification2("fish", new TokenSet(), 1.0));
    private static final Record2 test_record_2 = new Record2(3, "haddock", new Classification2("mammal", new TokenSet(), 1.0));
    private static final Record2 test_record_3 = new Record2(3, "osprey", new Classification2("mammal", new TokenSet(), 1.0));

    private static final Record2 gold_standard_record_1 = new Record2(3, "haddock", new Classification2("fish", new TokenSet(), 1.0));
    private static final Record2 gold_standard_record_2 = new Record2(3, "cow", new Classification2("mammal", new TokenSet(), 1.0));
    private static final Record2 gold_standard_record_3 = new Record2(3, "osprey", new Classification2("bird", new TokenSet(), 1.0));

    private Bucket2 classified_records;
    private Bucket2 gold_standard_records;
    private StrictConfusionMatrix2 matrix;

    @Before
    public void setUp() throws Exception {

        classified_records = new Bucket2();
        gold_standard_records = new Bucket2();
    }

    @Test
    public void emptyBucketsYieldNoClassifications() throws InvalidCodeException, InconsistentCodingException, UnknownDataException {

        initMatrix();

        assertEquals(0, matrix.getNumberOfClassifications());
    }

    @Test(expected = UnknownDataException.class)
    public void classificationWithNoGoldStandardThrowsException() throws InvalidCodeException, InconsistentCodingException, UnknownDataException {

        classified_records.add(test_record_1);

        initMatrix();
    }

    @Test(expected = InvalidCodeException.class)
    public void classificationToCodeNotInGoldStandardThrowsException() throws InvalidCodeException, InconsistentCodingException, UnknownDataException {

        classified_records.add(test_record_2);
        gold_standard_records.add(gold_standard_record_1);

        initMatrix();
    }

    @Test
    public void singleClassificationCountedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException {

        classified_records.add(test_record_1);
        gold_standard_records.add(gold_standard_record_1);

        initMatrix();

        assertEquals(1, matrix.getNumberOfClassifications());
    }

    @Test(expected = InconsistentCodingException.class)
    public void inconsistentCodingThrowsException() throws InconsistentCodingException, InvalidCodeException, UnknownDataException {

        classified_records.add(test_record_1, test_record_2);
        gold_standard_records.add(gold_standard_record_1, gold_standard_record_2);

        initMatrix();
    }

    @Test(expected = UnknownDataException.class)
    public void unknownClassificationThrowsException() throws UnknownDataException, InvalidCodeException, InconsistentCodingException {

        classified_records.add(test_record_1, test_record_3);
        gold_standard_records.add(gold_standard_record_1, gold_standard_record_2);

        initMatrix();
    }

    @Test
    public void perCodeClassificationsCountedCorrectly() throws InvalidCodeException, InconsistentCodingException, UnknownDataException {

        classified_records.add(test_record_1, test_record_3);
        gold_standard_records.add(gold_standard_record_1, gold_standard_record_2, gold_standard_record_3);

        initMatrix();

        Map<String, Integer> classification_counts = matrix.getClassificationCounts();
        assertEquals(3, classification_counts.size());
        assertEquals(1, (int) classification_counts.get("fish"));
        assertEquals(1, (int) classification_counts.get("mammal"));
        assertEquals(0, (int) classification_counts.get("bird"));
    }

    private void initMatrix() throws InvalidCodeException, InconsistentCodingException, UnknownDataException {

        matrix = new StrictConfusionMatrix2(classified_records, gold_standard_records);
    }
}
