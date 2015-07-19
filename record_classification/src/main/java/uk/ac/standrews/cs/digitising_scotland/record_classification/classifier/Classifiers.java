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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.composite.ClassifierPlusExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ensemble.EnsembleVotingClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.exact_match.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.string_similarity.StringSimilarityClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.string_similarity.StringSimilarityMetrics;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Classifiers that are accessible via the command-line interface.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public enum Classifiers implements Supplier<Classifier> {

    EXACT_MATCH(ExactMatchClassifier::new),

    STRING_SIMILARITY_LEVENSHTEIN(() -> new StringSimilarityClassifier(StringSimilarityMetrics.LEVENSHTEIN.get())),
    STRING_SIMILARITY_JARO_WINKLER(() -> new StringSimilarityClassifier(StringSimilarityMetrics.JARO_WINKLER.get())),
    STRING_SIMILARITY_JACCARD(() -> new StringSimilarityClassifier(StringSimilarityMetrics.JACCARD.get())),
    STRING_SIMILARITY_DICE(() -> new StringSimilarityClassifier(StringSimilarityMetrics.DICE.get())),

    OLR(OLRClassifier::new),

    EXACT_MATCH_PLUS_STRING_SIMILARITY_LEVENSHTEIN(
            () -> new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetrics.LEVENSHTEIN.get()))),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_JARO_WINKLER(
            () -> new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetrics.JARO_WINKLER.get()))),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_JACCARD(
            () -> new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetrics.JACCARD.get()))),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_DICE(
            () -> new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetrics.DICE.get()))),
    EXACT_MATCH_PLUS_OLR(
            () -> new ClassifierPlusExactMatchClassifier(new OLRClassifier())),
    VOTING_ENSEMBLE(
            () -> new EnsembleVotingClassifier(Arrays.asList(STRING_SIMILARITY_LEVENSHTEIN.get(), STRING_SIMILARITY_DICE.get(), STRING_SIMILARITY_JACCARD.get(), STRING_SIMILARITY_JARO_WINKLER.get(), OLR.get())));

    private Supplier<Classifier> supplier;

    Classifiers(Supplier<Classifier> supplier) {

        this.supplier = supplier;
    }

    public Classifier get() {

        return supplier.get();
    }

    public static Collection<Supplier<Classifier>> getStringSimilarityClassifiers() {

        return Arrays.asList(STRING_SIMILARITY_DICE, STRING_SIMILARITY_JACCARD,
                STRING_SIMILARITY_JARO_WINKLER, STRING_SIMILARITY_LEVENSHTEIN);
    }
}
