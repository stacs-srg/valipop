/*
 * Copyright 2014 Digitising Scotland project:
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
package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.io.IOException;
import java.util.Set;

/**
 * A holder class for an {@link ExactMatchClassifier}.
 * Provides convenience methods for classifying records and buckets.
 */
public class ExactMatchPipeline implements IPipeline {

    private ExactMatchClassifier classifier;

    private Bucket fullyClassified;

    /**
     * Instantiates a new exact match pipeline.
     *
     * @param exactMatchClassifier the exact match classifier
     */
    public ExactMatchPipeline(final ExactMatchClassifier exactMatchClassifier) {

        this.classifier = exactMatchClassifier;
        fullyClassified = new Bucket();
    }

    /**
     * Attempt to classify everything in a bucket.
     * If a record has an exact match is put in the classified bucket.
     * If a record does not have an exact match then the record is discarded.
     * The bucket with classified records is returned.
     *
     * @param bucket the bucket to classify
     * @return the bucket of exact matched records
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket) throws IOException {

        Bucket partialClassified = new Bucket();

        for (Record record : bucket) {

            String description = record.getDescription();

            final Set<Classification> result = classifier.classify(new TokenSet(description));
            if (result != null) {
                record.addClassificationsToDescription(description, result);
            }

            if (record.isFullyClassified()) {
                fullyClassified.addRecordToBucket(record);
            } else {
                partialClassified.addRecordToBucket(record);
            }
        }

        return partialClassified;
    }

    @Override
    public Bucket getSuccessfullyClassified() {

        return fullyClassified;
    }
}
