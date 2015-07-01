/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.process;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.security.*;
import java.time.*;
import java.util.*;

/**
 * Captures the shared knowledge among the {@link Step steps} of a {@link uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationProcess classification process}.
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
    private Duration training_time;
    private Duration evaluation_classification_time;

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
     * Sets the gold standard records of this context.
     *
     * @param gold_standard the gold standards to set
     */
    public void setGoldStandard(final Bucket gold_standard) {

        this.gold_standard = gold_standard;
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
    public Bucket getClassifiedUnseenRecords() {

        return classified_unseen_records;
    }

    /**
     * Sets the classified unseen records of this context.
     *
     * @param classified_unseen_records the classified unseen records to set
     */
    public void setClassifiedUnseenRecords(final Bucket classified_unseen_records) {

        this.classified_unseen_records = classified_unseen_records;
    }

    /**
     * Gets the classification metrics of this context.
     *
     * @return the classification metrics of this context, or {@code null}
     */
    public ClassificationMetrics getClassificationMetrics() {

        return classification_metrics;
    }

    /**
     * Sets the classification metrics of this context.
     *
     * @param classification_metrics the classification metrics to set
     */
    public void setClassificationMetrics(final ClassificationMetrics classification_metrics) {

        this.classification_metrics = classification_metrics;
    }

    /**
     * Gets the records that are used to train the classifier of this context.
     *
     * @return the records that are used to train the classifier of this context, or {@code null} if the records are not set.
     */
    public Bucket getTrainingRecords() {

        return training_records;
    }

    /**
     * Sets the records that are used to train the classifier of this context.
     *
     * @param training_records the records to be used for training the classifier of this context
     */
    public void setTrainingRecords(Bucket training_records) {

        this.training_records = training_records;
    }

    /**
     * Gets the confusion matrix of this context.
     *
     * @return the confusion matrix of this context, or {@code null} if no confusion matrix is set
     */
    public ConfusionMatrix getConfusionMatrix() {

        return confusion_matrix;
    }

    /**
     * Sets the confusion matrix of this context.
     *
     * @param confusion_matrix the confusion matrix to set
     */
    public void setConfusionMatrix(ConfusionMatrix confusion_matrix) {

        this.confusion_matrix = confusion_matrix;
    }

    /**
     * Gets the time it took to train the classifier in this context.
     *
     * @return the time it took to train the classifier in this context, or {@code null} if the classifier is not trained
     */
    public Duration getTrainingTime() {

        return training_time;
    }

    /**
     * Sets the time it took to train the classifier in this context.
     *
     * @param training_time the time it took to train the classifier in this context.
     */
    public void setTrainingTime(Duration training_time) {

        this.training_time = training_time;
    }

    /**
     * Gets the time it took to classify the evaluation records by the classifier in this context.
     *
     * @return the time it took to classify the evaluation records by the classifier in this context, or {@code null} if the classifier is not evaluated
     */
    public Duration getEvaluationClassificationTime() {

        return evaluation_classification_time;
    }

    /**
     * Sets the time it took to classify the evaluation records by the classifier in this context.
     *
     * @param evaluation_classification_time the time it took to classify the evaluation records by the classifier in this context
     */
    public void setEvaluationClassificationTime(Duration evaluation_classification_time) {

        this.evaluation_classification_time = evaluation_classification_time;
    }
}
