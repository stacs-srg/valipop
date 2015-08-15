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
import org.simmetrics.metrics.DiceSimilarity;
import org.simmetrics.metrics.JaccardSimilarity;
import org.simmetrics.metrics.JaroWinkler;
import org.simmetrics.metrics.Levenshtein;

import java.util.function.Supplier;

/**
 * The metrics by which to measure similarity between a pair of strings.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public enum StringSimilarityMetrics implements Supplier<SimilarityMetric> {

    /**
     * @see {@link JaccardSimilarity}.
     **/
    JACCARD(() -> new StringSimilarityMetricWrapper("JACCARD"), new SetMetricAdapter(new JaccardSimilarity<>())),

    /**
     * @see {@link Levenshtein}.
     **/
    LEVENSHTEIN(() -> new StringSimilarityMetricWrapper("LEVENSHTEIN"), new Levenshtein()),

    /**
     * @see {@link JaroWinkler}
     **/
    JARO_WINKLER(() -> new StringSimilarityMetricWrapper("JARO_WINKLER"), new JaroWinkler()),

    /**
     * @see {@link DiceSimilarity}.
     **/
    DICE(() -> new StringSimilarityMetricWrapper("DICE"), new SetMetricAdapter(new DiceSimilarity<>()));

    private Supplier<SimilarityMetric> supplier;
    private StringMetric metric;

    StringSimilarityMetrics(Supplier<SimilarityMetric> supplier, StringMetric metric) {

        this.supplier = supplier;
        this.metric = metric;
    }

    public SimilarityMetric get() {
        return supplier.get();
    }

    StringMetric getStringMetric() {

        return metric;
    }
}
