/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import old.record_classification_old.datastructures.tokens.*;
import org.apache.commons.lang3.*;
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests {@link EnsembleClassifier}.
 *
 * @author Masih Hajiarab Derkani
 */
public class EnsembleClassifierTest {

    private static final String RECORD_DATA = "record";
    private static final String SIMILAR_RECORD_DATA = "recrod";
    private static final String DISSIMILAR_RECORD_DATA = "fish";
    private static final Record TRAINING_RECORD = new Record(1, RECORD_DATA, new Classification("media", new TokenSet(), 1.0));
    private static final EnsembleClassifier.ResolutionStrategy DUMMY_RESOLUTION_STRATEGY = new DummyResolutionStrategy();

    private Bucket training_records;
    private EnsembleClassifier classifier;
    private Classifier exact_match_classifier;
    private Classifier string_similarity_classifier;

    @Before
    public void setUp() throws Exception {

        exact_match_classifier = new ExactMatchClassifier();
        string_similarity_classifier = new StringSimilarityClassifier(StringSimilarityMetric.JARO_WINKLER);
        final HashSet<Classifier> classifiers = new HashSet<>();
        classifiers.add(exact_match_classifier);
        classifiers.add(string_similarity_classifier);

        classifier = new EnsembleClassifier(classifiers, DUMMY_RESOLUTION_STRATEGY);
        training_records = new Bucket();
        training_records.add(TRAINING_RECORD);
    }

    @Test
    public void testTrain() throws Exception {

        assertEquals(Classification.UNCLASSIFIED, exact_match_classifier.classify(RECORD_DATA));
        assertEquals(Classification.UNCLASSIFIED, string_similarity_classifier.classify(RECORD_DATA));
        assertEquals(Classification.UNCLASSIFIED, string_similarity_classifier.classify(SIMILAR_RECORD_DATA));
        assertEquals(Classification.UNCLASSIFIED, string_similarity_classifier.classify(DISSIMILAR_RECORD_DATA));
        classifier.train(training_records);
        assertNotEquals(Classification.UNCLASSIFIED, exact_match_classifier.classify(RECORD_DATA));
        assertNotEquals(Classification.UNCLASSIFIED, string_similarity_classifier.classify(RECORD_DATA));
        assertNotEquals(Classification.UNCLASSIFIED, string_similarity_classifier.classify(SIMILAR_RECORD_DATA));
        assertEquals(Classification.UNCLASSIFIED, string_similarity_classifier.classify(DISSIMILAR_RECORD_DATA));

    }

    @Test
    public void testClassify() throws Exception {

        assertEquals(Classification.UNCLASSIFIED, classifier.classify(RECORD_DATA));
        assertEquals(Classification.UNCLASSIFIED, classifier.classify(SIMILAR_RECORD_DATA));
        assertEquals(Classification.UNCLASSIFIED, classifier.classify(DISSIMILAR_RECORD_DATA));
        classifier.train(training_records);
        assertNotEquals(Classification.UNCLASSIFIED, classifier.classify(RECORD_DATA));
        assertNotEquals(Classification.UNCLASSIFIED, classifier.classify(SIMILAR_RECORD_DATA));
        assertEquals(Classification.UNCLASSIFIED, classifier.classify(DISSIMILAR_RECORD_DATA));

    }

    @Test
    public void testSerialization() {

        assertEquals(classifier, SerializationUtils.deserialize(SerializationUtils.serialize(classifier)));
        classifier.train(training_records);
        assertEquals(classifier, SerializationUtils.deserialize(SerializationUtils.serialize(classifier)));
    }
}

