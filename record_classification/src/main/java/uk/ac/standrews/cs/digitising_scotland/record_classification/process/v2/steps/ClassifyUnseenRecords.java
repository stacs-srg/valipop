package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

/**
 * Classifies unseen records and stores the results in a classification process {@link Context context}.
 *
 * @author Masih Hajiarab Derkani
 */
public class ClassifyUnseenRecords implements Step {

    private static final long serialVersionUID = 292143932733171808L;
    private final Bucket unseen_records;

    /**
     * Instantiates a new unseen record classification step.
     *
     * @param unseen_records the unseen records to classify
     */
    public ClassifyUnseenRecords(Bucket unseen_records) {

        this.unseen_records = unseen_records;
    }

    @Override
    public void perform(final Context context) {

        final Classifier classifier = context.getClassifier();
        final Bucket classified_unseen_records = classifier.classify(unseen_records);

        context.setClassifiedUnseenRecords(classified_unseen_records);
    }
}
