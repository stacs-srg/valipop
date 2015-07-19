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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ensemble;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;

import java.io.Serializable;
import java.util.*;

/**
 * Classifies data with the aid of a collection of classifiers and chooses between classifications using a given {@link ResolutionStrategy}.
 *
 * @author Masih Hajiarab Derkani
 */
public class EnsembleClassifier implements Classifier {

    private static final long serialVersionUID = 6432371860423757296L;

    private List<Classifier> classifiers;
    private ResolutionStrategy resolution_strategy;

    /**
     * Needed for JSON deserialization.
     */
    public EnsembleClassifier() {
    }

    /**
     * Instantiates a new ensemble classifier.
     *
     * @param classifiers         the classifiers
     * @param resolution_strategy the strategy by which to decide a single classification between multiple classifications
     */
    public EnsembleClassifier(Collection<Classifier> classifiers, ResolutionStrategy resolution_strategy) {

        this.classifiers = new ArrayList<>(classifiers);
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
            final Classification classification = classifier.classify(data);
            candidate_classifications.put(classifier, classification);
        }

        return resolution_strategy.resolve(candidate_classifications);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + "[" + concatenateClassifierNames(classifiers) + "]";
    }

    public String toString() {

        return getName();
    }

    private String concatenateClassifierNames(List<Classifier> classifiers) {

        String result = "";
        for (Classifier classifier : classifiers) {
            if (result.length() > 0) result += ",";
            result += classifier.getName();
        }
        return result;
    }

    @Override
    public String getDescription() {

        return "Classifies using a set of classifiers";
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        final EnsembleClassifier that = (EnsembleClassifier) other;
        return Objects.equals(classifiers, that.classifiers) && Objects.equals(resolution_strategy, that.resolution_strategy);
    }

    @Override
    public int hashCode() {

        return Objects.hash(classifiers, resolution_strategy);
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
