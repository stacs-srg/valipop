package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import old.record_classification_old.classifiers.closestmatchmap.CarsonSimilarity;
import old.record_classification_old.datastructures.tokens.TokenSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;

import static org.junit.Assert.*;

/**
 * Tests {@link StringSimilarityClassifier}. 
 * 
 * @author masih
 */
public class StringSimilarityClassifierTest {


    StringSimilarityClassifier classifier = new StringSimilarityClassifier(new CarsonSimilarity<>());
    public static final String RECORD_DATA = "record";
    public static final String SIMILAR_RECORD_DATA = "recrod";
    public static final String DISSIMILAR_RECORD_DATA = "fish";
    public static final Record RECORD = new Record(1, RECORD_DATA, new Classification("media", new TokenSet(), 1.0));
    public static final Bucket TRAINING_RECORDS = new Bucket();

    @Before
    public void setUp() throws Exception {
        TRAINING_RECORDS.add(RECORD);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = NullPointerException.class)
    public void testNullSimilarityMetric() throws Exception {
        new StringSimilarityClassifier(null);
    }

    @Test
    public void testTrain() throws Exception {

        assertNull(classifier.classify(RECORD_DATA));
        classifier.train(TRAINING_RECORDS);
        assertNotNull(classifier.classify(RECORD_DATA));
    }

    @Test
    public void testClassify() throws Exception {
        assertNull(classifier.classify(RECORD_DATA));
        assertNull(classifier.classify(SIMILAR_RECORD_DATA));
        classifier.train(TRAINING_RECORDS);
        assertNotNull(classifier.classify(SIMILAR_RECORD_DATA));
        assertNull(classifier.classify(DISSIMILAR_RECORD_DATA));
        
    }
}