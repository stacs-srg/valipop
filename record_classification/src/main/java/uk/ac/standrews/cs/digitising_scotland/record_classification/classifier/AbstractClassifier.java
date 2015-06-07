package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;

public abstract class AbstractClassifier implements Classifier {

    public Bucket classify(final Bucket bucket) {

        Bucket classified = new Bucket();

        for (Record record : bucket) {

            final String data = record.getData();
            Classification classification = classify(data);

            if (classification == null) {
                classification = Classification.UNCLASSIFIED;
            }
            classified.add(new Record(record.getId(), data, classification));
        }

        return classified;
    }
}
