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

import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

/**
 * The metrics by which to measure similarity between a pair of strings.
 * This enumeration exists to accommodate serialisation of {@link InterfaceStringMetric}.
 *
 * @author Masih Hajiarab Derkani
 */
public enum StringSimilarityMetric {

    /** @see {@link JaccardSimilarity}. **/
    JACCARD(new JaccardSimilarity()),

    /** @see {@link BlockDistance}. **/
    BLOCK_DISTANCE(new BlockDistance()),

    /** @see {@link Levenshtein}. **/
    LEVENSHTEIN(new Levenshtein()),

    /** @see {@link JaroWinkler} **/
    JARO_WINKLER(new JaroWinkler()),

    /** @see {@link ChapmanLengthDeviation}. **/
    CHAPMAN_LENGTH_DEVIATION(new ChapmanLengthDeviation()),

    /** @see {@link DiceSimilarity}. **/
    DICE(new DiceSimilarity());
    
    // The annotation is to suppress warnings in intellij; the IDE wrongly warns of non-serializable field in enums:
    // http://docs.oracle.com/javase/8/docs/platform/serialization/spec/serial-arch.html#a6469
    // The issue is reported to JetBrains. The annontation is to be removed once the issue is rectified.
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final InterfaceStringMetric metric;

    StringSimilarityMetric(final InterfaceStringMetric metric) {

        this.metric = metric;
    }

    /**
     * Calculates the similarity between a given pair of strings.
     *
     * @param one the first string
     * @param other the second string
     * @return the similarity between the inclusive range of {@code 0} (no similarity) and {@code 1} (exact match)
     */
    public float getSimilarity(String one, String other) {

        return metric.getSimilarity(one, other);
    }
}
