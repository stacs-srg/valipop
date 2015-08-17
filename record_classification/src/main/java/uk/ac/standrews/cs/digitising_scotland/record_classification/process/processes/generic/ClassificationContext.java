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
import uk.ac.standrews.cs.util.tools.InfoLevel;

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
    private Bucket training_records;
    private Bucket evaluation_records;
    private Bucket unseen_records;
    private Bucket classified_unseen_records;

    private Classifier classifier;
    private ConfusionMatrix confusion_matrix;
    private ClassificationMetrics classification_metrics;

    private Duration training_time;
    private Duration classification_time;
    private int number_of_evaluation_records_including_duplicates;

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

        clearGoldStandardRecords();
        clearTrainingRecords();
        clearEvaluationRecords();
        clearUnseenRecords();
        clearClassifiedUnseenRecords();
    }

    public void clearGoldStandardRecords() {

        // Allow multiple data sets with potentially clashing ids to be added to gold standard.
        gold_standard_records = new Bucket(true);
    }

    public void clearTrainingRecords() {

        // Allow multiple data sets with potentially clashing ids to be added to training records.
        training_records = new Bucket(true);
    }

    public void clearEvaluationRecords() {

        // Allow multiple data sets with potentially clashing ids to be added to evaluation records.
        evaluation_records = new Bucket(true);
    }

    public void clearUnseenRecords() {

        unseen_records = new Bucket();
    }

    public void clearClassifiedUnseenRecords() {

        classified_unseen_records = new Bucket();
    }

    /**
     * Gets the random number generator.
     *
     * @return the random number generator.
     */
    public Random getRandom() {

        return random;
    }

    public Bucket getGoldStandardRecords() {

        return gold_standard_records;
    }

    /**
     * Adds the given records to the bucket of records that are used to train the classifier of this context.
     *
     * @param gold_standard_records the records to be added
     */
    public void setGoldStandardRecords(Bucket gold_standard_records) {

        this.gold_standard_records = gold_standard_records;
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

    /**
     * Gets the evaluation records in this context.
     *
     * @return the evaluation records, or {@code null} if not set
     */
    public Bucket getEvaluationRecords() {

        return evaluation_records;
    }

    /**
     * Adds the given records to the bucket for evaluating the classifier.
     * Duplicates are discarded, and existing classifications stripped.
     *
     * @param evaluation_records the records to be added
     */
    public void addEvaluationRecords(final Bucket evaluation_records) {

        number_of_evaluation_records_including_duplicates = evaluation_records.size();

        final Bucket unique_evaluation_records = evaluation_records.makeUniqueDataRecords();
        final Bucket stripped_unique_evaluation_records = unique_evaluation_records.makeStrippedRecords();

        stripped_unique_evaluation_records.forEach(this.evaluation_records::add);
    }

    /**
     * Gets the records that are to be classified.
     *
     * @return the records that are to be classified.
     */
    public Bucket getUnseenRecords() {

        return unseen_records;
    }

    /**
     * Adds the given records to the bucket of records that are to be classified.
     *
     * @param unseen_records the records to be added
     */
    public void setUnseenRecords(final Bucket unseen_records) {

        this.unseen_records = unseen_records;
    }

    /**
     * Gets the records that are classified by the classifier.
     *
     * @return the records that are classified by the classifier.
     */
    public Bucket getClassifiedUnseenRecords() {

        return classified_unseen_records;
    }

    /**
     * Adds the given records to the bucket of records that have been classified.
     *
     * @param classified_unseen_records the records to be added
     */
    public void addClassifiedUnseenRecords(final Bucket classified_unseen_records) {

        classified_unseen_records.forEach(this.classified_unseen_records::add);
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

//    public InfoLevel getVerbosity() {
//
//        return verbosity;
//    }
//
//    public void setVerbosity(InfoLevel verbosity) {
//
//        this.verbosity = verbosity;
//    }

    public int getNumberOfEvaluationRecordsIncludingDuplicates() {

        return number_of_evaluation_records_including_duplicates;
    }
}
