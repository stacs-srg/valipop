package uk.ac.standrews.cs.digitising_scotland.record_classification.multiple_classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

import java.util.*;
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
    private final Classifier core_classifier;
    private final double classification_confidence_threshold;

    public MultipleClassifier(Classifier core_classifier, double classification_confidence_threshold) {

        if (classification_confidence_threshold < 0 || classification_confidence_threshold > 1) {
            throw new IllegalArgumentException("confidence threshold must be within inclusive range of 0.0 to 1.0");
        }

        this.core_classifier = core_classifier;
        this.classification_confidence_threshold = classification_confidence_threshold;
    }

    public List<Classification> classify(String data) {

        final List<List<String>> token_combinations = generateCombinations(new TokenList(data));
        final List<CandidateClassification> candidate_classifications = generateCandidateClassifications(token_combinations);
        final List<List<CandidateClassification>> candidate_combinations = generateCombinations(candidate_classifications);
        final List<List<CandidateClassification>> valid_candidate_combinations = removeInvalidCombinations(candidate_combinations);
        final Optional<List<CandidateClassification>> fittest_combination = fittest(valid_candidate_combinations);

        return fittest_combination.isPresent() ? toClassificationList(fittest_combination.get()) : UNCLASSIFIED_CLASSIFICATION_LIST;
    }

    private <T> List<List<T>> generateCombinations(Collection<T> input) {
        //TODO parameterise strategy to generate combinations?
        //        return Combinations.powerset(input);
        return Combinations.all(input);
    }

    private List<CandidateClassification> generateCandidateClassifications(final List<List<String>> token_combinations) {

        final List<CandidateClassification> candidate_classifications = new ArrayList<>();

        for (List<String> token_combination : token_combinations) {

            final String data_element = String.join(TOKEN_JOIN_DELIMITER, token_combination);
            final Classification classification = core_classifier.classify(data_element);

            if (isAcceptable(classification)) {
                candidate_classifications.add(new CandidateClassification(token_combination, classification));
            }
        }
        return candidate_classifications;
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

        private List<String> tokens;
        private Classification classification;
        private final int hashcode;

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

            return isTokensDisjoint(other) && isClassificationDistinct(other);
        }

        private boolean isClassificationDistinct(final CandidateClassification other) {

            final String code = classification.getCode();
            final String other_code = other.classification.getCode();
            return !code.startsWith(other_code) && !other_code.startsWith(code);
        }

        private boolean isTokensDisjoint(final CandidateClassification other) {return Collections.disjoint(tokens, other.tokens);}

        /**
         * Gets the goodness measure of this candidate.
         * The higher the fitness value the fitter candidate.
         *
         * @return the goodness measure of this candidate, where higher is better.
         */
        double fitness() {

            return tokens.size() * classification.getConfidence();
        }

        private Classification getClassification() {

            return classification;
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

        @Override
        public int hashCode() {

            return hashcode;
        }
    }
}
