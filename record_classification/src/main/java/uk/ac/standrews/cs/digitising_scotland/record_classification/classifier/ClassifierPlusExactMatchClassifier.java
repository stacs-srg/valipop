package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;


import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;

public class ClassifierPlusExactMatchClassifier implements Classifier {

    private final Classifier classifier;
    private final ExactMatchClassifier exact_match_classifier;


    public ClassifierPlusExactMatchClassifier(Classifier classifier) {

        this.classifier = classifier;
        exact_match_classifier = new ExactMatchClassifier();
    }

    @Override
    public void train(final Bucket bucket) throws Exception {

        classifier.train(bucket);
        exact_match_classifier.train(bucket);
    }

    @Override
    public Classification classify(String data) throws Exception {

        Classification result = exact_match_classifier.classify(data);

        if (result != Classification.UNCLASSIFIED) {
            return result;
        }
        else {
            return classifier.classify(data);
        }
    }
}
