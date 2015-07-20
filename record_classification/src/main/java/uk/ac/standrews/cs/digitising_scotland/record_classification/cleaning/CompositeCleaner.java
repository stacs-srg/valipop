package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;

import java.util.List;

public class CompositeCleaner implements Cleaner {

    private final List<Cleaner> cleaners;

    public CompositeCleaner(List<Cleaner> cleaners) {

        this.cleaners = cleaners;
    }

    @Override
    public Bucket apply(Bucket records) {

        Bucket result = records;

        for (Cleaner cleaner : cleaners) {
            result = cleaner.apply(result);
        }

        return result;
    }
}
