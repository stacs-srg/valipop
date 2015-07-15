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
package uk.ac.standrews.cs.digitising_scotland.record_classification.model;

import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.DuplicateRecordIdExceptionTemp;

public class BucketTest {

    private static final Record[] RECORDS_WITH_DUPLICATE_IDS = new Record[]{
            new Record(1, "abc", new Classification("class1", new TokenSet("abc"), 1.0)),
            new Record(2, "def", new Classification("class2", new TokenSet("def"), 1.0)),
            new Record(2, "ghi", new Classification("class3", new TokenSet("ghi"), 1.0)),
            new Record(3, "bcd", new Classification("class4", new TokenSet("bcd"), 1.0)),
            new Record(4, "efg", new Classification("class5", new TokenSet("efg"), 1.0))
    };

    @Test(expected= DuplicateRecordIdExceptionTemp.class)
    public void duplicateRecordIdsDetected() {

        new Bucket(RECORDS_WITH_DUPLICATE_IDS);
    }
}
