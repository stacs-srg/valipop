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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;

import java.io.Serializable;
import java.time.Duration;
import java.util.Random;

/**
 * Captures the shared knowledge among the {@link Step steps} of a {@link ClassificationProcess classification process}.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public class ClassificationContext implements Serializable {

    private static final long serialVersionUID = -6389479358148790573L;
    private static final InfoLevel DEFAULT_VERBOSITY = InfoLevel.SHORT_SUMMARY;

    private Random random;

    private Bucket gold_standard_records;
    private Bucket evaluation_records;
    private Bucket training_records;
    private Bucket classified_unseen_records;

    private Classifier classifier;
    private ConfusionMatrix confusion_matrix;
    private ClassificationMetrics classification_metrics;

    private Duration training_time;
    private Duration classification_time;
    private InfoLevel verbosity;

    // Needed for JSON deserialization.
    public ClassificationContext() {
    }

    /**
     * Instantiates a new classification context.
     *
     * @param random the random number generator
     */
    public ClassificationContext(Classifier classifier, Random random) {

        this.classifier = classifier;
        this.random = random;

        gold_standard_records = new Bucket();
        evaluation_records = new Bucket();
        training_records = new Bucket();

        verbosity = DEFAULT_VERBOSITY;
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
     * Gets the evaluation records in this context.
     *
     * @return the evaluation records, or {@code null} if not set
     */
    public Bucket getEvaluationRecords() {

        return evaluation_records;
    }

    /**
     * Adds the given records to the bucket of records that are used to evaluate the classifier of this context.
     *
     * @param evaluation_records the records to be added
     */
    public void addEvaluationRecords(final Bucket evaluation_records) {

        evaluation_records.forEach(this.evaluation_records::add);
    }

    public void setEvaluationRecords(final Bucket evaluation_records) {

        this.evaluation_records = evaluation_records;
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
     * Adds the given records to the bucket of records that are used to train the classifier of this context.
     *
     * @param training_records the records to be added
     */
    public void addTrainingRecords(Bucket training_records) {

        training_records.forEach(this.training_records::add);
    }

    public void setTrainingRecords(final Bucket training_records) {

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
    public Duration getClassificationTime() {

        return classification_time;
    }

    /**
     * Sets the time it took to classify the evaluation records by the classifier in this context.
     *
     * @param evaluation_classification_time the time it took to classify the evaluation records by the classifier in this context
     */
    public void setClassificationTime(Duration evaluation_classification_time) {

        this.classification_time = evaluation_classification_time;
    }

    public Bucket getGoldStandardRecords() {

        return gold_standard_records;
    }

    public void setGoldStandardRecords(Bucket gold_standard) {

        this.gold_standard_records = gold_standard;
    }

    public InfoLevel getVerbosity() {

        return verbosity;
    }

    public void setVerbosity(InfoLevel verbosity) {

        this.verbosity = verbosity;
    }
}
