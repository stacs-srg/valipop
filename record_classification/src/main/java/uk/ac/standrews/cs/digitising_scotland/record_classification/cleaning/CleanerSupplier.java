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

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public enum CleanerSupplier implements Supplier<Cleaner> {

    PUNCTUATION(PunctuationCleaner::new, "Removes punctuation characters"),
    LOWER_CASE(LowerCaseCleaner::new, "Converts text to lower case"),
    STOP_WORDS(EnglishStopWordCleaner::new, "Removes English stop words"),
    PORTER_STEM(StemmingCleaner::new, "Performs stemming using Porter algorithm"),

    CONSISTENT_CLASSIFICATION_CLEANER_CORRECT(() -> ConsistentClassificationCleaner.CORRECT, "Corrects the classification of any inconsistently classified records to the most popular"),
    CONSISTENT_CLASSIFICATION_CLEANER_REMOVE(() -> ConsistentClassificationCleaner.REMOVE, "Removes any inconsistently classified records"),
    TRIM_CLASSIFICATION_CODE(TrimClassificationCodesCleaner::new, "Removes white-space characters fom the beginning/end of classification codes"),

    COMBINED(() -> new CompositeCleaner(Arrays.asList(PUNCTUATION.get(), LOWER_CASE.get(), STOP_WORDS.get(), PORTER_STEM.get(), CONSISTENT_CLASSIFICATION_CLEANER_CORRECT.get(), TRIM_CLASSIFICATION_CODE.get())), "Applies all available text cleaners and corrects inconsistent classifications");

    private final Supplier<Cleaner> supplier;
    private final String description;

    CleanerSupplier(Supplier<Cleaner> supplier, String description) {

        this.supplier = supplier;
        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    public Cleaner get() {

        return supplier.get();
    }
}
