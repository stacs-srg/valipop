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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;

import java.util.Arrays;

/**
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public enum Cleaners implements Cleaner {

    PUNCTUATION(new PunctuationCleaner(), "Removes punctuation characters"),
    LOWER_CASE(new LowerCaseCleaner(), "Converts text to lower case"),
    STOP_WORDS(new EnglishStopWordCleaner(), "Removes English stop words"),
    PORTER_STEM(new StemmingCleaner(), "Performs stemming using Porter algorithm"),

    CONSISTENT_CLASSIFICATION_CLEANER_CORRECT(ConsistentClassificationCleaner.CORRECT, "Corrects the classification of any inconsistently classified records to the most popular"),
    CONSISTENT_CLASSIFICATION_CLEANER_REMOVE(ConsistentClassificationCleaner.REMOVE, "Removes any inconsistently classified records"),

    COMBINED(new CompositeCleaner(Arrays.asList(PUNCTUATION, LOWER_CASE, STOP_WORDS, PORTER_STEM, CONSISTENT_CLASSIFICATION_CLEANER_CORRECT)), "Applies all available text cleaners and corrects inconsistent classifications");

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
    public Bucket apply(final Bucket bucket) {

        return cleaner.apply(bucket);
    }
}
