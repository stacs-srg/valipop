package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;

public interface ClassificationProcess {

    /**
     * Performs classification and returns the records that were not classified.
     * @return the records that were not classified
     */
    Bucket performClassification() throws Exception;
}
