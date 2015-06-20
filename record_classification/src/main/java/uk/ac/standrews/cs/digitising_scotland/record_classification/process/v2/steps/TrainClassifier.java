package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

/**
 * Trains a classifier in the context of a classification process using the {@link Context#getTrainingRecords() training records} in the context .
 *
 * @author Masih Hajiarab Derkani
 */
public class TrainClassifier implements Step {

    private static final long serialVersionUID = 5825366701064269040L;

    @Override
    public void perform(final Context context) throws Exception {

        final Classifier classifier = context.getClassifier();
        final Bucket training_records = context.getTrainingRecords();
        classifier.train(training_records);
    }
}
