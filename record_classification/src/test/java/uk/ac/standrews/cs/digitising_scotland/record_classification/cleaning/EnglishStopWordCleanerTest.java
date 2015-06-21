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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import old.record_classification_old.datastructures.tokens.*;
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests {@link EnglishStopWordCleaner}.
 *
 * @author Masih Hajiarab Derkani
 */
public class EnglishStopWordCleanerTest {

    private static final Map<String, String> TEST_STOP_WORDS = new HashMap<>();

    static {
        TEST_STOP_WORDS.put("this and that and the", "");
        TEST_STOP_WORDS.put("this and the fish", "fish");
        TEST_STOP_WORDS.put("a fish and the tank", "fish tank");
        TEST_STOP_WORDS.put("stop the words", "stop words");
    }

    private EnglishStopWordCleaner cleaner;

    @Before
    public void setUp() throws Exception {

        cleaner = new EnglishStopWordCleaner();
    }

    @Test
    public void testClean() throws Exception {

        final Bucket expected = new Bucket();
        final Bucket unclean = new Bucket();
        for (Map.Entry<String, String> entry : TEST_STOP_WORDS.entrySet()) {
            expected.add(new Record(1, entry.getValue()));
            unclean.add(new Record(1, entry.getKey()));
        }

        final Bucket actual = cleaner.clean(unclean);
        assertEquals(expected, actual);
    }

    @Test
    public void testCleanRecord() throws Exception {

        for (Map.Entry<String, String> entry : TEST_STOP_WORDS.entrySet()) {

            final Record expected = new Record(1, entry.getValue());
            final Record actual = cleaner.cleanRecord(new Record(1, entry.getKey()));
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testCleanClassification() throws Exception {

        for (Map.Entry<String, String> entry : TEST_STOP_WORDS.entrySet()) {

            final Classification expected = new Classification("code", new TokenSet(entry.getValue()), 0.1);
            final Classification unclean_classification = new Classification("code", new TokenSet(entry.getKey()), 0.1);
            final Classification actual = cleaner.cleanClassification(entry.getValue(), unclean_classification);
            assertEquals(expected, actual);
        }

        assertEquals(Classification.UNCLASSIFIED, cleaner.cleanClassification("", Classification.UNCLASSIFIED));
    }

    @Test
    public void testRemoveStopWords() throws Exception {

        for (Map.Entry<String, String> entry : TEST_STOP_WORDS.entrySet()) {

            final String expected = entry.getValue();
            final String actual = EnglishStopWordCleaner.removeStopWords(entry.getKey());
            assertEquals(expected, actual);
        }
    }
}
