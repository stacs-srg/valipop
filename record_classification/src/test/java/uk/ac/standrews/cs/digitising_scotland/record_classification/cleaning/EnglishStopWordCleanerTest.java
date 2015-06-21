package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import old.record_classification_old.datastructures.tokens.*;
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests {@link EnglishStopWordCleaner}.
 *
 * @author Masih Hajiarab Derkani
 */
public class EnglishStopWordCleanerTest {

    private static final Map<String, String> TEST_STOP_WORDS = new HashMap<>();

    static {
        TEST_STOP_WORDS.put("this and that and the", "");
        TEST_STOP_WORDS.put("this and the fish", "fish");
        TEST_STOP_WORDS.put("a fish and the tank", "fish tank");
        TEST_STOP_WORDS.put("stop the words", "stop words");
    }

    private EnglishStopWordCleaner cleaner;

    @Before
    public void setUp() throws Exception {

        cleaner = new EnglishStopWordCleaner();
    }

    @Test
    public void testClean() throws Exception {

        final Bucket expected = new Bucket();
        final Bucket unclean = new Bucket();
        for (Map.Entry<String, String> entry : TEST_STOP_WORDS.entrySet()) {
            expected.add(new Record(1, entry.getValue()));
            unclean.add(new Record(1, entry.getKey()));
        }

        final Bucket actual = cleaner.clean(unclean);
        assertEquals(expected, actual);
    }

    @Test
    public void testCleanRecord() throws Exception {

        for (Map.Entry<String, String> entry : TEST_STOP_WORDS.entrySet()) {

            final Record expected = new Record(1, entry.getValue());
            final Record actual = cleaner.cleanRecord(new Record(1, entry.getKey()));
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testCleanClassification() throws Exception {

        for (Map.Entry<String, String> entry : TEST_STOP_WORDS.entrySet()) {

            final Classification expected = new Classification("code", new TokenSet(entry.getValue()), 0.1);
            final Classification unclean_classification = new Classification("code", new TokenSet(entry.getKey()), 0.1);
            final Classification actual = cleaner.cleanClassification(entry.getValue(), unclean_classification);
            assertEquals(expected, actual);
        }

        assertEquals(Classification.UNCLASSIFIED, cleaner.cleanClassification("", Classification.UNCLASSIFIED));
    }

    @Test
    public void testRemoveStopWords() throws Exception {

        for (Map.Entry<String, String> entry : TEST_STOP_WORDS.entrySet()) {

            final String expected = entry.getValue();
            final String actual = EnglishStopWordCleaner.removeStopWords(entry.getKey());
            assertEquals(expected, actual);
        }
    }
}
