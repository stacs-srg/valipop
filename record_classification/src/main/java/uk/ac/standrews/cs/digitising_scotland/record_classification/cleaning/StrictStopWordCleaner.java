package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import java.util.*;

/**
 * Returns an empty string if the entire data exactly matches any of the stop words.
 *
 * @author Masih Hajiarab Derkani
 */
public class StrictStopWordCleaner implements TextCleaner {

    private static final String EMPTY_STRING = "";
    private final Collection<String> stop_words;

    public StrictStopWordCleaner(Collection<String> stop_words) {

        this.stop_words = stop_words;
    }

    @Override
    public String cleanData(final String data) {

        return stop_words.contains(data) ? EMPTY_STRING : data;
    }
}
