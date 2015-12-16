package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.logistic_regression;

import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.function.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class OLRClassifierTest extends ClassifierTest {

    public OLRClassifierTest(final Supplier<Classifier> factory) {

        super(factory);
    }

    @Test
    public void testClassifiesToUnclassifiedIfUntrained() throws Exception {

        final OLRClassifier classifier = new OLRClassifier();
        
        assertEquals(Classification.UNCLASSIFIED, classifier.doClassify("fish"));
        assertEquals(Classification.UNCLASSIFIED, classifier.doClassify("sssda"));
        assertEquals(Classification.UNCLASSIFIED, classifier.doClassify("rrr"));
        assertEquals(Classification.UNCLASSIFIED, classifier.doClassify(""));
    }
}
