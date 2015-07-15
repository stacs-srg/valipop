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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.string_similarity;

import org.simmetrics.StringMetric;

import java.io.Serializable;

/**
 * The metrics by which to measure similarity between a pair of strings.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public class StringSimilarityMetricWrapper implements SimilarityMetric, Serializable {

    // This is transient to help with JSON serialization.
    private transient StringMetric metric;
    private double static_confidence;
    private String name;

    /**
     * Needed for JSON deserialization.
     */
    StringSimilarityMetricWrapper() {
    }

    StringSimilarityMetricWrapper(final String name, double static_confidence) {

        this.name = name;
        this.static_confidence = static_confidence;
    }

    /**
     * Calculates the similarity between a given pair of strings.
     *
     * @param one   the first string
     * @param other the second string
     * @return the similarity between the inclusive range of {@code 0} (no similarity) and {@code 1} (exact match)
     */
    public float getSimilarity(String one, String other) {

        loadMetricIfNecessary();
        return metric.compare(one, other);
    }

    private void loadMetricIfNecessary() {

        if (metric == null) {
            metric = StringSimilarityMetrics.valueOf(name).getStringMetric();
        }
    }

    public String getDescription() {

        loadMetricIfNecessary();
        return metric.toString();
    }

    public String getName() {

        return name;
    }

    public double getStaticConfidence() {

        return static_confidence;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringSimilarityMetricWrapper && ((StringSimilarityMetricWrapper)obj).getName().equals(name);
    }

    public String toString() {

        return getName();
    }
}
