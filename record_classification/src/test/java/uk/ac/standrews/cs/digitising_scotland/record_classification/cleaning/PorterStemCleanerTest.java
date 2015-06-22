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

import java.util.*;

/**
 * Tests {@link PorterStemCleaner}.
 *
 * @author Masih Hajiarab Derkani
 */
public class PorterStemCleanerTest extends TokenFilterCleanerTest {

    public PorterStemCleanerTest() {

        super(new PorterStemCleaner(), new HashMap<String, String>() {

            {
                put("driving", "drive");
                put("cats driving cars at the pub", "cat drive car at the pub");
                put("classification", "classif");
                put("this", "thi");
                put("this and that and the other stuffing shoots the shingles off the roofs", "thi and that and the other stuf shoot the shingl off the roof");
            }
        });
    }
}
