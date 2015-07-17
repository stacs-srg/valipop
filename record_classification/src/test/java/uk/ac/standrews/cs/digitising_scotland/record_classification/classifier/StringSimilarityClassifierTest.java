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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenList;

import java.util.*;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/***
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@RunWith(Parameterized.class)
public class StringSimilarityClassifierTest extends ClassifierTest {

    private static final Record[] TRAINING_RECORDS = new Record[]{
            new Record(1, "trail", new Classification("class1", new TokenList("trail"), 1.0)),
            new Record(2, "mouse", new Classification("class2", new TokenList("mouse"), 1.0)),
            new Record(3, "through", new Classification("class3", new TokenList("through"), 1.0)),
            new Record(4, "quick brown fox", new Classification("class4", new TokenList("quick brown fox"), 1.0)),
            new Record(5, "lazy dog", new Classification("class4", new TokenList("lazy dog"), 1.0))
    };

    private static final Record[] TEST_RECORDS = new Record[]{
            new Record(1, TEST_VALUES[0]),
            new Record(2, TEST_VALUES[1]),
            new Record(3, TEST_VALUES[2]),
            new Record(4, TEST_VALUES[3]),
            new Record(5, TEST_VALUES[4])
    };

    private static final Map<String, String> SIMILARITY_MAP = new HashMap<>();

    static {
        SIMILARITY_MAP.put("trial", "trail");
        SIMILARITY_MAP.put("house", "mouse");
        SIMILARITY_MAP.put("thought", "through");
        SIMILARITY_MAP.put("quick brown fish", "quick brown fox");
        SIMILARITY_MAP.put("lazy dogs", "lazy dog");
    }

    private Bucket training_bucket;
    private Bucket test_bucket;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> generateData() {

        List<Object[]> result = new ArrayList<>();

        for (Supplier<Classifier> factory : Classifiers.getStringSimilarityClassifiers()) {
            result.add(new Object[]{factory});
        }

        return result;
    }

    public StringSimilarityClassifierTest(Supplier<Classifier> factory) {

        super(factory);

        training_bucket = new Bucket(TRAINING_RECORDS);
        test_bucket = new Bucket(TEST_RECORDS);
    }

    @Test
    public void trainedClassifierReturnsCodeOfSimilarValueForIndividualRecord() {

        Classifier classifier = factory.get();

        classifier.train(training_bucket);

        for (String value : TEST_VALUES) {

            assertClassifiedSimilarly(value, classifier.classify(value));
        }
    }

    @Test
    public void trainedClassifierReturnsCodeOfSimilarValueForBucket() {

        Classifier classifier = factory.get();

        classifier.train(training_bucket);

        final Bucket classified_bucket = classifier.classify(test_bucket);

        for (String value : TEST_VALUES) {

            assertClassifiedSimilarly(value, getClassificationFromBucket(value, classified_bucket));
        }
    }

    protected void assertClassifiedSimilarly(String test_value, Classification test_classification) {

        final String most_similar_data_in_training_set = SIMILARITY_MAP.get(test_value);
        final Classification classification_in_training_set = getClassificationFromBucket(most_similar_data_in_training_set, training_bucket);

        //noinspection ConstantConditions
        assertEquals(classification_in_training_set.getCode(), test_classification.getCode());
    }

    private Classification getClassificationFromBucket(String value, Bucket bucket) {

        for (Record record : bucket) {
            if (record.getData().equals(value)) {
                return record.getClassification();
            }
        }
        return null;
    }
}
