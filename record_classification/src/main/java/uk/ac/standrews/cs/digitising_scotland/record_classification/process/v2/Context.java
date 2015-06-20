package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.security.*;
import java.util.*;

/**
 * Captures the shared knowledge among the {@link Step steps} of a {@link ClassificationProcess classification process}.
 *
 * @author Masih Hajiarab Derkani
 */
public class Context implements Serializable {

    private static final long serialVersionUID = -6389479358148790573L;
    private final Random random;
    private Classifier classifier;
    private Bucket gold_standard;
    private Bucket training_records;
    private Bucket classified_unseen_records;

    private ConfusionMatrix confusion_matrix;
    private ClassificationMetrics classification_metrics;

    /**
     * Instantiates a new classification context with a non-deterministic random number generator.
     */
    public Context() {

        this(new SecureRandom());
    }

    /**
     * Instantiates a new classification context.
     *
     * @param random the random number generator
     */
    public Context(Random random) {

        this.random = random;
    }

    /**
     * Gets the random number generator.
     *
     * @return the random number generator.
     */
    public Random getRandom() {

        return random;
    }

    /**
     * Gets the gold standard data in this context.
     *
     * @return the gold standard records, or {@code null} if not set
     */
    public Bucket getGoldStandard() {

        return gold_standard;

    }

    /**
     * Gets classifier in this context.
     *
     * @return the classifier in this context
     */
    public Classifier getClassifier() {

        return classifier;
    }

    /**
     * Sets the classifier of this context.
     *
     * @param classifier the classifier to set
     */
    public void setClassifier(final Classifier classifier) {

        this.classifier = classifier;
    }

    /**
     * Gets the records that are classified by the classifier.
     *
     * @return the records that are classified by the classifier, or {@code null} if no records have been classified.
     */
    public Bucket getClassifiedRecords() {

        return classified_unseen_records;
    }

    /**
     * Sets the gold standard records of this context.
     *
     * @param gold_standard the gold standards to set
     */
    public void setGoldStandard(final Bucket gold_standard) {

        this.gold_standard = gold_standard;
    }

    /**
     * Sets the classified unseen records of this context.
     *
     * @param classified_unseen_records the classified unseen records to set
     */
    public void setClassifiedUnseenRecords(final Bucket classified_unseen_records) {

        this.classified_unseen_records = classified_unseen_records;
    }

    public void setClassificationMetrics(final ConcreteClassificationMetrics classification_metrics) {

        this.classification_metrics = classification_metrics;
    }

    public ClassificationMetrics getClassificationMetrics() {

        return classification_metrics;
    }

    public Bucket getTrainingRecords() {

        return training_records;
    }

    public void setTrainingRecords(Bucket training_records) {

        this.training_records = training_records;
    }

    public ConfusionMatrix getConfusionMatrix() {

        return confusion_matrix;
    }

    public void setConfusionMatrix(ConfusionMatrix confusion_matrix) {

        this.confusion_matrix = confusion_matrix;
    }
}
