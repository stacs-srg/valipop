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
 * Utility class used to test cleaners based on {@link TokenFilterCleaner}.
 *
 * @author Masih Hajiarab Derkani
 */
public abstract class TokenFilterCleanerTest {

    private final TokenFilterCleaner cleaner;
    private final Map<String, String> input_expected_map;

    public TokenFilterCleanerTest(final TokenFilterCleaner cleaner, Map<String, String> input_expected_map) {

        this.cleaner = cleaner;
        this.input_expected_map = input_expected_map;
    }

    @Test
    public void testClean() throws Exception {

        final Bucket expected = new Bucket();
        final Bucket unclean = new Bucket();
        for (Map.Entry<String, String> entry : input_expected_map.entrySet()) {
            expected.add(new Record(1, entry.getValue()));
            unclean.add(new Record(1, entry.getKey()));
        }

        final Bucket actual = cleaner.clean(unclean);
        assertEquals(expected, actual);
    }

    @Test
    public void testCleanRecord() throws Exception {

        for (Map.Entry<String, String> entry : input_expected_map.entrySet()) {

            final Record expected = new Record(1, entry.getValue());
            final Record actual = cleaner.cleanRecord(new Record(1, entry.getKey()));
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testCleanClassification() throws Exception {

        for (Map.Entry<String, String> entry : input_expected_map.entrySet()) {

            final Classification expected = new Classification("code", new TokenSet(entry.getValue()), 0.1);
            final Classification unclean_classification = new Classification("code", new TokenSet(entry.getKey()), 0.1);
            final Classification actual = cleaner.cleanClassification(entry.getValue(), unclean_classification);
            assertEquals(expected, actual);
        }

        assertEquals(Classification.UNCLASSIFIED, cleaner.cleanClassification("", Classification.UNCLASSIFIED));
    }

    @Test
    public void testCleanData() throws Exception {

        for (Map.Entry<String, String> entry : input_expected_map.entrySet()) {

            final String expected = entry.getValue();
            final String actual = cleaner.cleanData(entry.getKey());
            assertEquals(expected, actual);
        }
    }
}
