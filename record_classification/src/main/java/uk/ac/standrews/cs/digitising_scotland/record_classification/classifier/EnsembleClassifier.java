package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;

import java.util.*;

/**
 * Classifies data with the aid of a group of classifiers and chooses between classifications using a given {@link ResolutionStrategy}.
 *
 * @author Masih Hajiarab Derkani
 */
public class EnsembleClassifier extends AbstractClassifier {

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

        Map<Classifier, Classification> candidate_classifications = new HashMap<>();

        for (Classifier classifier : classifiers) {
            candidate_classifications.put(classifier, classifier.classify(data));
        }

        return resolution_strategy.resolve(candidate_classifications);
    }

    /**
     * Captures the strategy by which to resolve a single classification from multiple classifications.
     */
    interface ResolutionStrategy {

        
        /**
         * Resolves a single classification from classifications produced by different classifiers.
         *
         * @param candidate_classifications the candidate classifications
         * @return a single classification.
         */
        Classification resolve(Map<Classifier, Classification> candidate_classifications);

    }
}
