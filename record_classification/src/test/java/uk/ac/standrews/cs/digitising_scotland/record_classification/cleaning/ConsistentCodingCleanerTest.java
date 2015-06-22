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
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import static org.junit.Assert.*;

public class ConsistentCodingCleanerTest extends AbstractMetricsTest {

    private Bucket bucket;

    @Before
    public void setup() {

        bucket = new Bucket();
        bucket.add(haddock_correct, haddock_incorrect, osprey_incorrect);
    }

    @Test(expected = InconsistentCodingException.class)
    public void check() throws Exception {

        ConsistentCodingCleaner.CHECK.clean(bucket);
    }

    @Test
    public void remove() throws Exception {

        Bucket cleaned = ConsistentCodingCleaner.REMOVE.clean(bucket);

        assertEquals(1, cleaned.size());
        assertTrue(cleaned.contains(osprey_incorrect));
    }

    @Test
    public void correct() throws Exception {

        // In this case there are two classifications of haddock as mammal and only one as fish, so the latter should be corrected to mammal.

        bucket.add(new Record(3, "haddock", new Classification("mammal", new TokenSet(), 1.0)));

        Bucket cleaned = ConsistentCodingCleaner.CORRECT.clean(bucket);

        assertEquals(4, cleaned.size());

        for (Record record : cleaned) {

            if (record.getData().equals("haddock")) {
                assertTrue(record.getClassification().getCode().equals("mammal"));
            }
        }
    }

}
