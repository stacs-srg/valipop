package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression.OLRClassifier;

public class OLRWithExactMatchClassifier extends ClassifierPlusExactMatchClassifier {

    public OLRWithExactMatchClassifier() {

        super(new OLRClassifier());
    }
}
