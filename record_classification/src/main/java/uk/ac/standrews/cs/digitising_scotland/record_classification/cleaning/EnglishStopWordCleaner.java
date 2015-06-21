package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import old.record_classification_old.datastructures.tokens.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.util.*;

/**
 * Removes english stop words from records.
 *
 * @author Masih Hajiarab Derkani
 */
public class EnglishStopWordCleaner implements Cleaner {

    private static final long serialVersionUID = 3018841867887670242L;
    private static final Set<?> STOP_WORDS = EnglishAnalyzer.getDefaultStopSet();
    private static final char SPACE = ' ';

    @Override
    public Bucket clean(final Bucket bucket) throws Exception {

        final Bucket cleaned_bucket = new Bucket();
        for (Record record : bucket) {

            final Record cleaned_record = cleanRecord(record);
            cleaned_bucket.add(cleaned_record);
        }
        return cleaned_bucket;
    }

    protected Record cleanRecord(final Record record) throws Exception {

        final int id = record.getId();
        final String data = record.getData();
        final String cleaned_data = removeStopWords(data);
        final Classification classification = record.getClassification();
        final Classification cleaned_classification = cleanClassification(cleaned_data, classification);

        return new Record(id, cleaned_data, cleaned_classification);
    }

    protected Classification cleanClassification(final String cleaned_data, final Classification old_classification) {

        final Classification cleaned_classification;
        if (old_classification.equals(Classification.UNCLASSIFIED)) {
            cleaned_classification = old_classification;
        }
        else {
            final String code = old_classification.getCode();
            //TODO Not sure if token set cleaning is correct; discuss with Graham
            final TokenSet tokens = new TokenSet(cleaned_data);
            final double confidence = old_classification.getConfidence();

            cleaned_classification = new Classification(code, tokens, confidence);
        }
        return cleaned_classification;
    }

    /**
     * Removes english stop words from a given string.
     *
     * @param data the string from which to remove stop words
     * @return the given string without english stop words
     * @throws IOException if an error occurs while removing stop words
     */
    protected static String removeStopWords(final String data) throws IOException {

        final TokenStream tokenizer_stream = new StandardTokenizer(Version.LUCENE_36, new StringReader(data.trim()));
        final TokenStream filter = new StopFilter(Version.LUCENE_36, tokenizer_stream, STOP_WORDS);
        final StringBuilder cleaned_data = new StringBuilder();
        final CharTermAttribute charTermAttribute = filter.addAttribute(CharTermAttribute.class);

        filter.reset();

        while (filter.incrementToken()) {
            String term = charTermAttribute.toString();
            cleaned_data.append(term).append(SPACE);
        }

        return cleaned_data.toString().trim();
    }
}
