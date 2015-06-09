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
package old.record_classification_old.classifiers.lookup;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import old.record_classification_old.datastructures.OriginalData;
import old.record_classification_old.datastructures.bucket.Bucket;
import old.record_classification_old.datastructures.records.Record;
import old.record_classification_old.datastructures.tokens.TokenSet;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the NGramSubstring class that grams are being produced correctly.
 * @author jkc25
 *
 */
@Ignore
public class NGramSubstringsTest {

    /** The n gram substrings. */
    private NGramSubstrings nGramSubstrings;

    /** The n gram substrings2. */
    private NGramSubstrings nGramSubstrings2;

    /** The n gram substrings record. */
    private NGramSubstrings nGramSubstringsRecord;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        nGramSubstrings = new NGramSubstrings("A test String");
        nGramSubstrings2 = new NGramSubstrings("A test String");
        int id = (int) Math.rint(Math.random() * 1000);

        OriginalData originalData = new OriginalData("A test Description", 2014, 1, "testFileName");
        Record record = new Record(id, originalData);
        Bucket bucketToClean = new Bucket();
        bucketToClean.addRecordToBucket(record);
        nGramSubstringsRecord = new NGramSubstrings(record.getDescription());
    }

    /**
     * Test get grams.
     */
    @Test
    public void testGetGrams() {

        List<TokenSet> grams = nGramSubstrings.getGrams();
        assertEquals(6, grams.size());
    }

    /**
     * Test get grams for record.
     */
    @Test
    public void testGetGramsForRecord() {

        List<TokenSet> grams = nGramSubstringsRecord.getGrams();
        assertEquals(6, grams.size());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals() {

        List<TokenSet> grams = nGramSubstrings.getGrams();
        List<TokenSet> grams2 = nGramSubstrings2.getGrams();
        assertEquals(nGramSubstrings, nGramSubstrings2);

        assertEquals(grams, grams2);
    }
}
