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

    STRING_SIMILARITY_LEVENSHTEIN(Classifiers::makeLevenshteinClassifier),
    STRING_SIMILARITY_JARO_WINKLER(Classifiers::makeJaroWinklerClassifier),
    STRING_SIMILARITY_JACCARD(Classifiers::makeJaccardClassifier),
    STRING_SIMILARITY_DICE(Classifiers::makeDiceClassifier),

    OLR(OLRClassifier::new),

    VOTING_ENSEMBLE(Classifiers::makeVotingEnsembleClassifier),

    EXACT_MATCH_PLUS_STRING_SIMILARITY_LEVENSHTEIN(Classifiers::makeExactMatchPlusLevenshteinClassifier),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_JARO_WINKLER(Classifiers::makeExactMatchPlusJaroWinklerClassifier),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_JACCARD(Classifiers::makeExactMatchPlusJaccardClassifier),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_DICE(Classifiers::makeExactMatchPlusDiceClassifier),
    EXACT_MATCH_PLUS_OLR(Classifiers::makeExactMatchPlusOLRClassifier),
    EXACT_MATCH_PLUS_VOTING_ENSEMBLE(Classifiers::makeExactMatchPlusVotingEnsembleClassifier);

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

    private static EnsembleVotingClassifier makeVotingEnsembleClassifier() {

        return new EnsembleVotingClassifier(Arrays.asList(
                STRING_SIMILARITY_LEVENSHTEIN.get(),
                STRING_SIMILARITY_DICE.get(),
                STRING_SIMILARITY_JACCARD.get(),
                STRING_SIMILARITY_JARO_WINKLER.get(),
                OLR.get()));
    }

    private static ClassifierPlusExactMatchClassifier makeExactMatchPlusVotingEnsembleClassifier() {
        return new ClassifierPlusExactMatchClassifier(makeVotingEnsembleClassifier());
    }

    private static ClassifierPlusExactMatchClassifier makeExactMatchPlusOLRClassifier() {
        return new ClassifierPlusExactMatchClassifier(new OLRClassifier());
    }

    private static ClassifierPlusExactMatchClassifier makeExactMatchPlusDiceClassifier() {
        return new ClassifierPlusExactMatchClassifier(makeDiceClassifier());
    }

    private static ClassifierPlusExactMatchClassifier makeExactMatchPlusJaccardClassifier() {
        return new ClassifierPlusExactMatchClassifier(makeJaccardClassifier());
    }

    private static ClassifierPlusExactMatchClassifier makeExactMatchPlusJaroWinklerClassifier() {
        return new ClassifierPlusExactMatchClassifier(makeJaroWinklerClassifier());
    }

    private static ClassifierPlusExactMatchClassifier makeExactMatchPlusLevenshteinClassifier() {
        return new ClassifierPlusExactMatchClassifier(makeLevenshteinClassifier());
    }
}
