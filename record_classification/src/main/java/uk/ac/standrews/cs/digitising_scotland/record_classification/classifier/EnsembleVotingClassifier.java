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

            Set< Set<Classification>> classifications_with_same_codes = partitionClassificationsWithSameCodes(candidate_classifications);

            Set<Classification> classifications_with_most_popular_code = getClassificationsWithMostPopularCode(classifications_with_same_codes);

            return classificationWithHighestConfidence(classifications_with_most_popular_code);
        }

        private Set<Set<Classification>> partitionClassificationsWithSameCodes(Map<Classifier, Classification> candidate_classifications) {

            Set<Set<Classification>> classification_sets = new HashSet<>();

            for (Classification classification : candidate_classifications.values()) {

                Set<Classification> other_classifications_with_this_code = findOtherClassificationsWithThisCode(classification_sets, classification.getCode());

                if (other_classifications_with_this_code == null) {
                    other_classifications_with_this_code = new HashSet<>();
                    classification_sets.add(other_classifications_with_this_code);
                }

                other_classifications_with_this_code.add(classification);
            }

            return classification_sets;
        }

        private Set<Classification> findOtherClassificationsWithThisCode(Set<Set<Classification>> classification_sets, String code) {

            for (Set<Classification> other_classifications : classification_sets) {

                for (Classification classification : other_classifications) {

                    if (classification.getCode().equals(code)) {
                        return other_classifications;
                    }
                }
            }
            return null;
        }

        private Set<Classification> getClassificationsWithMostPopularCode(Set<Set<Classification>> classifications_with_same_codes) {

            int max_size = 0;
            Set<Classification> classifications_with_popular_code = null;

            for (Set<Classification> classifications : classifications_with_same_codes) {

                if (classifications.size() > max_size) {
                    max_size = classifications.size();
                    classifications_with_popular_code = classifications;
                }
            }

            return classifications_with_popular_code;
        }

        private Classification classificationWithHighestConfidence(Set<Classification> classifications_with_most_popular_code) {

            double max_confidence = 0.0;
            Classification most_confident_classification = null;

            for (Classification classification : classifications_with_most_popular_code) {

                if (classification.getConfidence() > max_confidence) {
                    max_confidence = classification.getConfidence();
                    most_confident_classification = classification;
                }
            }

            return most_confident_classification;
        }
    }
}
