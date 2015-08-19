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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.util.tools.Logging;

import java.io.Serializable;
import java.util.Random;

/**
 * Basic classifier implementation.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public abstract class Classifier implements Serializable {

    /**
     * Trains the classifier on the given gold standard records, and performs internal evaluation.
     *
     * @param bucket                  the training data
     * @param internal_training_ratio the ratio of gold standard records to be used for training as opposed to internal evaluation
     * @param random                  a random number generator to use in selecting the records to use for internal evaluation
     */
    public abstract void trainAndEvaluate(final Bucket bucket, final double internal_training_ratio, final Random random);

    /**
     * Classifies a single data item.
     *
     * @param data the data to be classified
     * @return the resulting classification, or {@link Classification#UNCLASSIFIED} if the data cannot be classified
     */
    public abstract Classification classify(String data);

    /**
     * Classifies a bucket of data items.
     * If a record in the given bucket cannot be classified, its classification is set to {@link Classification#UNCLASSIFIED}.
     *
     * @param bucket the data to be classified
     * @return a new bucket containing the classified data
     */
    public Bucket classify(final Bucket bucket) {

        return classify(bucket, false);
    }

    protected Bucket classify(final Bucket bucket, boolean set_confidence) {

        Logging.setProgressIndicatorSteps(bucket.size());
        Logging.output(InfoLevel.LONG_SUMMARY, "\nClassifying...");

        final Bucket classified = new Bucket();

        for (Record record : bucket) {

            final String data = record.getData();

            Classification classification = classify(data);
            setConfidence(classification, set_confidence);
            classified.add(new Record(record.getId(), data, record.getOriginalData(), classification));

            Logging.progressStep(InfoLevel.LONG_SUMMARY);
        }
        Logging.output(InfoLevel.LONG_SUMMARY, "Done...\n");

        return classified;
    }

    protected void setConfidence(Classification classification, boolean set_confidence) {
    }

    public abstract String getName();

    public abstract String getDescription();
}
