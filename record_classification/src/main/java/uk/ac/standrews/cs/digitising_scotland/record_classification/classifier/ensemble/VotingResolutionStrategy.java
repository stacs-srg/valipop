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
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VotingResolutionStrategy implements EnsembleClassifier.ResolutionStrategy, Serializable {

    /**
     * Needed for JSON deserialization.
     */
    public VotingResolutionStrategy() {
    }

    @Override
    public Classification resolve(Map<Classifier, Classification> candidate_classifications) {

        Set<Set<Classification>> classifications_with_same_codes = partitionClassificationsWithSameCodes(candidate_classifications);

        Set<Set<Classification>> classifications_with_most_popular_code = getClassificationsWithMostPopularCode(classifications_with_same_codes);

        Map<Set<Classification>, Double> confidence_averages = getConfidenceAverages(classifications_with_most_popular_code);

        return classificationWithHighestConfidenceAverage(confidence_averages);
    }

    private Classification classificationWithHighestConfidenceAverage(Map<Set<Classification>, Double> confidence_averages) {

        double highest_confidence = 0.0;
        Set<Classification> classifications = null;

        for (Map.Entry<Set<Classification>, Double> entry : confidence_averages.entrySet()) {

            if (entry.getValue() > highest_confidence) {
                highest_confidence = entry.getValue();
                classifications = entry.getKey();
            }
        }

        if (classifications != null) {
            //noinspection LoopStatementThatDoesntLoop
            for (Classification classification : classifications) {
                return new Classification(classification.getCode(), classification.getTokenList(), averageConfidence(classifications));
            }
        }
        return Classification.UNCLASSIFIED;
    }

    private Map<Set<Classification>, Double> getConfidenceAverages(Set<Set<Classification>> classifications_with_most_popular_code) {

        Map<Set<Classification>, Double> confidence_averages = new HashMap<>();

        for (Set<Classification> classifications : classifications_with_most_popular_code) {
            confidence_averages.put(classifications, averageConfidence(classifications));
        }

        return confidence_averages;
    }

    private Double averageConfidence(Set<Classification> classifications) {

        double sum = 0.0;
        for (Classification classification : classifications) {
            sum += classification.getConfidence();
        }
        return sum / classifications.size();
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

    private Set<Set<Classification>> getClassificationsWithMostPopularCode(Set<Set<Classification>> classifications_with_same_codes) {

        int max_size = 0;

        for (Set<Classification> classifications : classifications_with_same_codes) {

            if (classifications.size() > max_size) {
                max_size = classifications.size();
            }
        }

        Set<Set<Classification>> classifications_with_popular_code = new HashSet<>();

        for (Set<Classification> classifications : classifications_with_same_codes) {

            if (classifications.size() == max_size) {
                classifications_with_popular_code.add(classifications);
            }
        }

        return classifications_with_popular_code;
    }
}
