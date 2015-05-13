package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractClassifier implements Classifier {

    public static Set<Classification> makeClassificationSet(Classification classification) {

        Set<Classification> result = new HashSet<>();
        result.add(classification);
        return result;
    }

    public static Classification getSingleClassification(Set<Classification> classifications) {

        for (Classification classification : classifications) {
            return classification;
        }
        return null;
    }
}
