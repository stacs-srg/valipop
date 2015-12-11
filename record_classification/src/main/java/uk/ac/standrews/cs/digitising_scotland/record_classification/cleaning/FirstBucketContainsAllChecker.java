package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;
import java.util.stream.*;

/**
 * Given a list of buckets checks whether a specific value in records of the first bucket contain all the record values from the remaining buckets.
 * The checked record value of the records is specified by {@link #getTargetValue(Record)}.
 * The check will return {@code false} if the given list of buckets is empty or only contains a single bucket.
 * The check is performed in parallel.
 *
 * @author Masih Hajiarab Derkani
 */
public abstract class FirstBucketContainsAllChecker implements Checker {

    private static final long serialVersionUID = -3005784531108907181L;

    @Override
    public boolean test(final List<Bucket> buckets) {

        return buckets.size() > 1 && containsAllValues(first(buckets), exceptFirst(buckets));
    }

    private static List<Bucket> exceptFirst(final List<Bucket> buckets) {return buckets.subList(1, buckets.size());}

    private static Bucket first(final List<Bucket> buckets) {return buckets.get(0);}

    private boolean containsAllValues(final Bucket source, final List<Bucket> targets) {

        final Set<String> source_data = collectReferenceValues(source);

        return targets.parallelStream().allMatch(target -> containsAllValues(source_data, target));
    }

    protected Set<String> collectReferenceValues(final Bucket source) {

        return source.parallelStream().map(this::getTargetValue).collect(Collectors.toSet());
    }

    private boolean containsAllValues(Set<String> source, Bucket target) {

        return target.parallelStream().map(this::getTargetValue).allMatch(source::contains);
    }

    protected abstract String getTargetValue(Record record);
}
