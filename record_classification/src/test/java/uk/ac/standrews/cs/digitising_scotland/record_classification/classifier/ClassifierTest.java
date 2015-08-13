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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/***
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@RunWith(Parameterized.class)
public class ClassifierTest {

    private static final double DELTA = 0.999;

    protected static final String[] TEST_VALUES = new String[]{"trial", "house", "thought", "quick brown fish", "lazy dogs"};

    protected static final Record[] TRAINING_RECORDS = new Record[]{
            new Record(1, "trail", new Classification("class1", new TokenList("trail"), 1.0, null)),
            new Record(2, "mouse", new Classification("class2", new TokenList("mouse"), 1.0, null)),
            new Record(3, "through", new Classification("class3", new TokenList("through"), 1.0, null)),
            new Record(4, "quick brown fox", new Classification("class4", new TokenList("quick brown fox"), 1.0, null)),
            new Record(5, "lazy dog", new Classification("class4", new TokenList("lazy dog"), 1.0, null))
    };

    protected static final Record[] TEST_RECORDS = new Record[]{
            new Record(1, TEST_VALUES[0]),
            new Record(2, TEST_VALUES[1]),
            new Record(3, TEST_VALUES[2]),
            new Record(4, TEST_VALUES[3]),
            new Record(5, TEST_VALUES[4])
    };

    protected Supplier<Classifier> factory;
    protected Bucket training_bucket;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> generateData() {

        List<Object[]> result = new ArrayList<>();

        for (Supplier<Classifier> factory : ClassifierSupplier.values()) {
            result.add(new Object[]{factory});
        }

        return result;
    }

    public ClassifierTest(Supplier<Classifier> factory) {

        this.factory = factory;
        training_bucket = new Bucket(TRAINING_RECORDS);
    }

    @Test
    public void untrainedClassifierReturnsUnclassified() {

        Classifier classifier = factory.get();

        for (String value : TEST_VALUES) {
            assertEquals(Classification.UNCLASSIFIED, classifier.classify(value));
        }
    }

    @Test
    public void exactMatchClassificationHasConfidenceOfOne() {

        if (factory == ClassifierSupplier.EXACT_MATCH) {

            Classifier classifier = factory.get();

            classifier.train(training_bucket);

            assertEquals(1.0, classifier.classify("trail").getConfidence(), DELTA);
            assertEquals(1.0, classifier.classify("through").getConfidence(), DELTA);
        }
    }
}
