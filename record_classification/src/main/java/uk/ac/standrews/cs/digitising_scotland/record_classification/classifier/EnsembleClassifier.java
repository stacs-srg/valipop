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

import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.util.*;

/**
 * Classifies data with the aid of a group of classifiers and chooses between classifications using a given {@link ResolutionStrategy}.
 *
 * @author Masih Hajiarab Derkani
 */
public class EnsembleClassifier extends AbstractClassifier {

    private static final long serialVersionUID = 6432371860423757296L;
    private final Set<Classifier> classifiers;
    private final ResolutionStrategy resolution_strategy;

    /**
     * Instantiates a new ensemble classifier.
     *
     * @param classifiers the classifiers
     * @param resolution_strategy the strategy by which to decide a single classification between multiple classifications
     */
    public EnsembleClassifier(Set<Classifier> classifiers, ResolutionStrategy resolution_strategy) {

        this.classifiers = classifiers;
        this.resolution_strategy = resolution_strategy;
    }

    @Override
    public void train(final Bucket bucket) {

        for (Classifier classifier : classifiers) {
            classifier.train(bucket);
        }
    }

    @Override
    public Classification classify(String data) {

        final Map<Classifier, Classification> candidate_classifications = new HashMap<>();

        for (Classifier classifier : classifiers) {
            candidate_classifications.put(classifier, classifier.classify(data));
        }

        return resolution_strategy.resolve(candidate_classifications);
    }

    /**
     * Captures the strategy by which to resolve a single classification from multiple classifications.
     */
    public interface ResolutionStrategy extends Serializable {

        /**
         * Resolves a single classification from classifications produced by different classifiers.
         *
         * @param candidate_classifications the candidate classifications
         * @return a single classification.
         */
        Classification resolve(Map<Classifier, Classification> candidate_classifications);

    }
}
