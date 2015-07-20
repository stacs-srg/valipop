package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.composite;


import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.string_similarity.StringSimilarityClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class StringSimilarityGroupWithSharedState implements Serializable {

    private List<StringSimilarityClassifier> classifiers;

    /**
     * Required for JSON deserialization.
     */
    public StringSimilarityGroupWithSharedState() {
    }

    public StringSimilarityGroupWithSharedState(List<StringSimilarityClassifier> classifiers) {

        this.classifiers = classifiers;
    }

    public List<StringSimilarityClassifier> getClassifiers() {

        return classifiers;
    }

    public void trainAll(Bucket bucket) {

        final StringSimilarityClassifier first_classifier = classifiers.get(0);
        first_classifier.train(bucket);

        setOtherClassifierStatesToFirst();
    }

    public void recoverFromSerialization() {

        setOtherClassifierStatesToFirst();
    }

    public void prepareForSerialization() {

        deleteOtherClassifierStates();
    }

    private void setOtherClassifierStatesToFirst() {

        Map<String, Classification> state_of_first_classifier = classifiers.get(0).readState();

        for (int i = 1; i < classifiers.size(); i++) {

            classifiers.get(i).writeState(state_of_first_classifier);
        }
    }

    private void deleteOtherClassifierStates() {

        for (int i = 1; i < classifiers.size(); i++) {

            classifiers.get(i).writeState(null);
        }
    }
}
