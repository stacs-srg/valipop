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

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.composite.StringSimilarityGroupWithSharedState;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ensemble.EnsembleVotingClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.exact_match.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.nb2.NaiveBayesClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.string_similarity.StringSimilarityClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.string_similarity.StringSimilarityMetrics;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * Classifiers that are accessible via the command-line interface.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public enum ClassifierSupplier implements Supplier<Classifier> {

    EXACT_MATCH(ExactMatchClassifier::new),

    STRING_SIMILARITY_LEVENSHTEIN(ClassifierSupplier::makeLevenshteinClassifier),
    STRING_SIMILARITY_JARO_WINKLER(ClassifierSupplier::makeJaroWinklerClassifier),
    STRING_SIMILARITY_JACCARD(ClassifierSupplier::makeJaccardClassifier),
    STRING_SIMILARITY_DICE(ClassifierSupplier::makeDiceClassifier),

    OLR(OLRClassifier::new),
    NAIVE_BAYES(NaiveBayesClassifier::new),

    VOTING_ENSEMBLE_EXACT_OLR_SIMILARITY(ClassifierSupplier::makeVotingEnsembleClassifierWithOLRAndStringSimilarity),
    VOTING_ENSEMBLE_EXACT_SIMILARITY(ClassifierSupplier::makeVotingEnsembleClassifierWithStringSimilarity);

    private Supplier<Classifier> supplier;

    ClassifierSupplier(Supplier<Classifier> supplier) {

        this.supplier = supplier;
    }

    public Classifier get() {

        return supplier.get();
    }

    public boolean isExactMatch() {

        return this == EXACT_MATCH;
    }

    public boolean isEnsemble() {

        return this == VOTING_ENSEMBLE_EXACT_OLR_SIMILARITY || this == VOTING_ENSEMBLE_EXACT_SIMILARITY;
    }

    public static Collection<Supplier<Classifier>> getStringSimilarityClassifiers() {

        return Arrays.asList(STRING_SIMILARITY_DICE, STRING_SIMILARITY_JACCARD,
                STRING_SIMILARITY_JARO_WINKLER, STRING_SIMILARITY_LEVENSHTEIN);
    }

    private static StringSimilarityClassifier makeDiceClassifier() {
        return new StringSimilarityClassifier(StringSimilarityMetrics.DICE.get());
    }

    private static StringSimilarityClassifier makeJaccardClassifier() {
        return new StringSimilarityClassifier(StringSimilarityMetrics.JACCARD.get());
    }

    private static StringSimilarityClassifier makeJaroWinklerClassifier() {
        return new StringSimilarityClassifier(StringSimilarityMetrics.JARO_WINKLER.get());
    }

    private static StringSimilarityClassifier makeLevenshteinClassifier() {

        return new StringSimilarityClassifier(StringSimilarityMetrics.LEVENSHTEIN.get());
    }

    private static EnsembleVotingClassifier makeVotingEnsembleClassifierWithOLRAndStringSimilarity() {

        return new EnsembleVotingClassifier(Arrays.asList((SingleClassifier) EXACT_MATCH.get(), (SingleClassifier) OLR.get()),
                new StringSimilarityGroupWithSharedState(Arrays.asList(
                        makeDiceClassifier(),
                        makeJaccardClassifier(),
                        makeJaroWinklerClassifier(),
                        makeLevenshteinClassifier()
                )));
    }

    private static EnsembleVotingClassifier makeVotingEnsembleClassifierWithStringSimilarity() {

        return new EnsembleVotingClassifier(Collections.singletonList((SingleClassifier) EXACT_MATCH.get()),
                new StringSimilarityGroupWithSharedState(Arrays.asList(
                        makeDiceClassifier(),
                        makeJaccardClassifier(),
                        makeJaroWinklerClassifier(),
                        makeLevenshteinClassifier()
                )));
    }
}
