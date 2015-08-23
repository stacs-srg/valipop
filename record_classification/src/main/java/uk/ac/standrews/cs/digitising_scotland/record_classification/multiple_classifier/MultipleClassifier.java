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
package uk.ac.standrews.cs.digitising_scotland.record_classification.multiple_classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Classifies a given string value to multiple {@link Classification classifications}.
 *
 * @author Masih Hajiarab Derkani
 */
public class MultipleClassifier {

    private static final List<Classification> UNCLASSIFIED_CLASSIFICATION_LIST = Collections.singletonList(Classification.UNCLASSIFIED);
    private static final CandidateClassificationListFitnessComparator CANDIDATE_CLASSIFICATION_LIST_FITNESS_COMPARATOR = new CandidateClassificationListFitnessComparator();
    private static final CharSequence TOKEN_JOIN_DELIMITER = " ";
    private static final TextCleaner AS_IS = data -> data;
    private static final BiPredicate<Classification, Classification> ALWAYS_DISTICT = (one, another) -> true;
    private final Classifier core_classifier;
    private final double classification_confidence_threshold;
    private final TextCleaner pre_classification_data_cleaner;
    private final BiPredicate<Classification, Classification> distict_classification_checker;

    public MultipleClassifier(Classifier core_classifier, double classification_confidence_threshold) {

        this(core_classifier, classification_confidence_threshold, AS_IS, ALWAYS_DISTICT);

    }

    public MultipleClassifier(Classifier core_classifier, double classification_confidence_threshold, TextCleaner pre_classification_data_cleaner, BiPredicate<Classification, Classification> distict_classification_checker) {

        if (classification_confidence_threshold < 0 || classification_confidence_threshold > 1) {
            throw new IllegalArgumentException("confidence threshold must be within inclusive range of 0.0 to 1.0");
        }

        this.core_classifier = core_classifier;
        this.classification_confidence_threshold = classification_confidence_threshold;
        this.pre_classification_data_cleaner = pre_classification_data_cleaner;
        this.distict_classification_checker = distict_classification_checker;
    }

    public List<Classification> classify(String data) {

        final String cleaned_data = cleanData(data).intern();
        final TokenList cleaned_data_tokens = new TokenList(cleaned_data);
        final List<List<String>> token_combinations = Combinations.all(cleaned_data_tokens);
        final List<CandidateClassification> candidate_classifications = generateCandidateClassifications(token_combinations);
        final List<List<CandidateClassification>> candidate_combinations = Combinations.powerset(candidate_classifications);
        final List<List<CandidateClassification>> valid_candidate_combinations = removeInvalidCombinations(candidate_combinations);
        final Optional<List<CandidateClassification>> fittest_combination = fittest(valid_candidate_combinations);

        return fittest_combination.isPresent() ? toClassificationList(fittest_combination.get()) : UNCLASSIFIED_CLASSIFICATION_LIST;
    }

    private String cleanData(final String data) {return pre_classification_data_cleaner.cleanData(data);}

    private List<CandidateClassification> generateCandidateClassifications(final List<List<String>> token_combinations) {

        final List<CandidateClassification> candidate_classifications = new ArrayList<>();

        for (final List<String> token_combination : token_combinations) {

            final Classification classification = classify(token_combination);

            if (isAcceptable(classification)) {
                candidate_classifications.add(new CandidateClassification(token_combination, classification));
            }
        }

        return candidate_classifications;
    }

    private Classification classify(final List<String> token_combination) {

        final String data_element = String.join(TOKEN_JOIN_DELIMITER, token_combination);
        return core_classifier.classify(data_element);
    }

    private boolean isAcceptable(final Classification classification) {return !classification.isUnclassified() && satisfiesConfidenceThreshold(classification);}

    private boolean satisfiesConfidenceThreshold(final Classification classification) {return classification.getConfidence() >= classification_confidence_threshold;}

    private List<Classification> toClassificationList(final List<CandidateClassification> candidate_classifications) {

        return candidate_classifications.stream().map(CandidateClassification::getClassification).collect(Collectors.toList());
    }

    private List<List<CandidateClassification>> removeInvalidCombinations(final List<List<CandidateClassification>> candidate_combinations) {

        final Iterator<List<CandidateClassification>> candidate_combinations_iterator = candidate_combinations.iterator();

        while (candidate_combinations_iterator.hasNext()) {

            final List<CandidateClassification> next_candidate_combination = candidate_combinations_iterator.next();
            if (!isValidCombination(next_candidate_combination)) {
                candidate_combinations_iterator.remove();
            }
        }

        return candidate_combinations;
    }

    private boolean isValidCombination(List<CandidateClassification> combination) {

        final int combination_size = combination.size();

        // We do not need to loop n^2 times, since isCombinableWith is a symmetric operation.
        for (int outer_index = 0; outer_index < combination_size; outer_index++) {

            final CandidateClassification one = combination.get(outer_index);
            for (int inner_index = outer_index + 1; inner_index < combination_size; inner_index++) {

                final CandidateClassification another = combination.get(inner_index);
                if (!one.isCombinableWith(another)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Optional<List<CandidateClassification>> fittest(List<List<CandidateClassification>> candidate_combinations) {

        return candidate_combinations.stream().max(CANDIDATE_CLASSIFICATION_LIST_FITNESS_COMPARATOR);

    }

    private static class CandidateClassificationListFitnessComparator implements Comparator<List<CandidateClassification>> {

        @Override
        public int compare(final List<CandidateClassification> one, final List<CandidateClassification> another) {

            return Double.compare(collectiveFitness(one), collectiveFitness(another));
        }

        private double collectiveFitness(final List<CandidateClassification> candidate_combination) {

            return candidate_combination.stream().mapToDouble(CandidateClassification::fitness).sum();

        }
    }

    private class CandidateClassification {

        private final int hashcode;
        private List<String> tokens;
        private Classification classification;

        CandidateClassification(final List<String> tokens, final Classification classification) {

            this.tokens = tokens;
            this.classification = classification;
            hashcode = Objects.hash(tokens, classification);
        }

        /**
         * Checks whether this candidate can be combined with the other candidate.
         * This operation is symmetric, i.e. {@code a.isCombinableWith(b) == b.isCombinableWith(a)}.
         *
         * @param other the other candidate
         * @return whether this candidate can occur in combination with the other candidate.
         */
        boolean isCombinableWith(CandidateClassification other) {

            return isTokensDisjoint(other) && isClassificationDistinct(other.classification);
        }

        private boolean isClassificationDistinct(final Classification other) {

            return distict_classification_checker.test(classification, other);
        }

        private boolean isTokensDisjoint(final CandidateClassification other) {return Collections.disjoint(tokens, other.tokens);}

        /**
         * Gets the goodness measure of this candidate.
         * The higher the fitness value the fitter candidate.
         *
         * @return the goodness measure of this candidate, where higher is better.
         */
        private double fitness() {

            return tokens.size() * classification.getConfidence();
        }

        private Classification getClassification() {

            return classification;
        }

        @Override
        public int hashCode() {

            return hashcode;
        }

        @Override
        public boolean equals(final Object other) {

            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            final CandidateClassification that = (CandidateClassification) other;
            return Objects.equals(tokens, that.tokens) && Objects.equals(classification, that.classification);
        }
    }
}
