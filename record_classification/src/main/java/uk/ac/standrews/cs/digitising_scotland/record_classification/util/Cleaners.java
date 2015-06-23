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
package uk.ac.standrews.cs.digitising_scotland.record_classification.util;

import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

/**
 * @author Masih Hajiarab Derkani
 */
public enum Cleaners implements Cleaner {

    STOP_WORDS(new EnglishStopWordCleaner(), "Removes English stop words"),
    PORTER_STEM(new PorterStemCleaner(), "Performs stemming using Porter algorithm"),
    CONSISTENT_CODING_CLEANER_NONE(ConsistentCodingCleaner.NONE, ""),
    CONSISTENT_CODING_CLEANER_CORRECT(ConsistentCodingCleaner.CORRECT, ""),
    CONSISTENT_CODING_CLEANER_CHECK(ConsistentCodingCleaner.CHECK, ""),
    CONSISTENT_CODING_CLEANER_REMOVE(ConsistentCodingCleaner.REMOVE, "");

    private final Cleaner cleaner;
    private final String description;

    Cleaners(Cleaner cleaner, String description) {

        this.cleaner = cleaner;
        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    @Override
    public Bucket clean(final Bucket bucket) throws Exception {

        return cleaner.clean(bucket);
    }
}
