package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;
import java.util.stream.*;

/**
 * Given a list of buckets checks whether the classification codes of the first bucket contains all the classification codes from the remaining buckets.
 * The check will return {@code false} if the given list of buckets is empty or only contains a single bucket.
 * The check is performed in parallel.
 *
 * @author Masih Hajiarab Derkani
 */
public class FirstBucketContainsAllCodesChecker extends FirstBucketContainsAllChecker {

    private static final long serialVersionUID = 5759446970510579358L;

    @Override
    protected String getTargetValue(Record record) {

        return record.getClassification().getCode();
    }

    @Override
    protected Set<String> collectReferenceValues(final Bucket source) {

        final Set<String> values = super.collectReferenceValues(source);
        values.add(Classification.UNCLASSIFIED_CODE);
        return values;
    }
}
