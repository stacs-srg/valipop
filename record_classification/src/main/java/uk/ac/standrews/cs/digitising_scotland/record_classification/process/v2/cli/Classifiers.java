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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.cli;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

/**
 * @author masih
 */
enum Classifiers implements Classifier {

    DUMMY(new DummyClassifier(), "A dummy classifier; for testing purposes"),
    EXACT_MATCH(new ExactMatchClassifier(), "Classifies based on exact match with training data"),
    STRING_SIMILARITY_JARO_WINKLER(new StringSimilarityClassifier(StringSimilarityMetric.JARO_WINKLER), "Classifies based on similarity of the string to the training data; uses Jaro Winkler algorithm ot clacuate similarity");

    private final Classifier classifier;
    private final String description;

    Classifiers(Classifier classifier, final String description) {

        this.classifier = classifier;
        this.description = description;
    }

    @Override
    public void train(final Bucket bucket) {

        classifier.train(bucket);
    }

    @Override
    public Classification classify(final String data) {

        return classifier.classify(data);
    }

    @Override
    public Bucket classify(final Bucket bucket) {

        return classifier.classify(bucket);
    }

    String getDescription() {

        return description;
    }
}
