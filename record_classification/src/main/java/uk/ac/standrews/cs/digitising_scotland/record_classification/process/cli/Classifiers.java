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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

/**
 * Classifiers that are accessible via the command-line interface.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public enum Classifiers implements Classifier {

    DUMMY(new DummyClassifier()),
    EXACT_MATCH(new ExactMatchClassifier()),

    STRING_SIMILARITY_LEVENSHTEIN(new StringSimilarityClassifier(StringSimilarityMetric.LEVENSHTEIN)),
    STRING_SIMILARITY_JARO_WINKLER(new StringSimilarityClassifier(StringSimilarityMetric.JARO_WINKLER)),
    STRING_SIMILARITY_JACCARD(new StringSimilarityClassifier(StringSimilarityMetric.JACCARD)),
    STRING_SIMILARITY_CHAPMAN_LENGTH_DEVIATION(new StringSimilarityClassifier(StringSimilarityMetric.CHAPMAN_LENGTH_DEVIATION)),
    STRING_SIMILARITY_DICE(new StringSimilarityClassifier(StringSimilarityMetric.DICE)),

    OLR(new OLRClassifier()),

    EXACT_MATCH_PLUS_STRING_SIMILARITY_LEVENSHTEIN(new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetric.LEVENSHTEIN))),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_JARO_WINKLER(new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetric.JARO_WINKLER))),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_JACCARD(new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetric.JACCARD))),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_CHAPMAN_LENGTH_DEVIATION(new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetric.CHAPMAN_LENGTH_DEVIATION))),
    EXACT_MATCH_PLUS_STRING_SIMILARITY_DICE(new ClassifierPlusExactMatchClassifier(new StringSimilarityClassifier(StringSimilarityMetric.DICE))),
    EXACT_MATCH_PLUS_OLR(new ClassifierPlusExactMatchClassifier(new OLRClassifier()));

    private final Classifier classifier;

    Classifiers(Classifier classifier) {

        this.classifier = classifier;
    }

    @Override
    public void train(final Bucket bucket) throws Exception {

        classifier.train(bucket);
    }

    @Override
    public Classification classify(final String data) throws Exception {

        return classifier.classify(data);
    }

    @Override
    public Bucket classify(final Bucket bucket) throws Exception {

        return classifier.classify(bucket);
    }

    @Override
    public String getName() {
        return classifier.getName();
    }

    @Override
    public String getDescription() {

        return classifier.getDescription();
    }
}
