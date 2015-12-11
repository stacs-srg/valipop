package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

/**
 * Given a list of buckets checks whether the record {@link Record#getData() data} of the first bucket contains all the record data from the remaining buckets.
 * The check will return {@code false} if the given list of buckets is empty or only contains a single bucket.
 * The check is performed in parallel.
 *
 * @author Masih Hajiarab Derkani
 */
public class FirstBucketContainsAllDataChecker extends FirstBucketContainsAllChecker {

    private static final long serialVersionUID = -576819366082147816L;

    @Override
    protected String getTargetValue(Record record) {

        return record.getData();
    }
}
