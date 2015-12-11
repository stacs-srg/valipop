package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;

/**
 * Checks whether any records are present that are unclassified.
 * A record is considered to be unclassified if its {@link Record#getClassification() classification} is {@code null} or is {@link Classification#isUnclassified() unclassified} as defined by the {@link Classification} class.
 * The check for unclassified records is performed in parallel.
 *
 * @author Masih Hajiarab Derkani
 */
public class UnclassifiedChecker implements Checker {

    private static final long serialVersionUID = 2465719719128993717L;

    @Override
    public boolean test(final List<Bucket> buckets) {

        return buckets.parallelStream().anyMatch(this::test);
    }

    @Override
    public boolean test(final Bucket bucket) {

        return bucket.parallelStream().anyMatch(this::isUnclassified);
    }

    protected boolean isUnclassified(final Record record) {

        final Classification classification = record.getClassification();
        return classification == null || classification.isUnclassified();
    }
}
