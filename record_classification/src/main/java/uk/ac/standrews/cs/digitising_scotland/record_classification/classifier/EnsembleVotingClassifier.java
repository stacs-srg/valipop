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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;

import java.util.*;

/**
 * @author Graham Kirby
 */
public class EnsembleVotingClassifier extends EnsembleClassifier {

    private static final long serialVersionUID = 6432371860467757296L;

    /**
     * Instantiates a new ensemble classifier.
     *
     * @param classifiers the classifiers
     */
    public EnsembleVotingClassifier(Collection<Classifier> classifiers) {

        super(classifiers, new VotingResolutionStrategy());
    }

    private static class VotingResolutionStrategy implements ResolutionStrategy {

        @Override
        public Classification resolve(Map<Classifier, Classification> candidate_classifications) {

            Map<Classification, Integer> classification_counts = countOccurrencesOfClassifications(candidate_classifications);

            return getMostPopularClassification(classification_counts);
        }

        private Map<Classification, Integer> countOccurrencesOfClassifications(Map<Classifier, Classification> candidate_classifications) {

            Map<Classification, Integer> classification_counts = new HashMap<>();

            for (Classification classification : candidate_classifications.values()) {

                if (!classification_counts.containsKey(classification)) {
                    classification_counts.put(classification, 0);
                }

                classification_counts.put(classification, classification_counts.get(classification) + 1);
            }

            return classification_counts;
        }

        private Classification getMostPopularClassification(Map<Classification, Integer> classification_counts) {

            Classification result = null;
            double max_confidence = 0.0;

            int occurrences_of_most_popular_classification = getMostPopularCount(classification_counts);

            for (Classification classification : classification_counts.keySet()) {
                if (classification_counts.get(classification) == occurrences_of_most_popular_classification) {
                    double confidence = classification.getConfidence();
                    if (confidence > max_confidence) {
                        result = classification;
                        max_confidence = confidence;
                    }
                }
            }

            return result;
        }

        private int getMostPopularCount(Map<Classification, Integer> classification_counts) {

            int max_count = 0;

            for (Classification classification : classification_counts.keySet()) {
                int classification_count = classification_counts.get(classification);
                if (classification_count > max_count) {
                    max_count = classification_count;
                }
            }

            return max_count;
        }
    }
}
