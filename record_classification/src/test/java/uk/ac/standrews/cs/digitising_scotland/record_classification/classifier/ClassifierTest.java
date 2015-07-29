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
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;

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

    protected static final String[] TEST_VALUES = new String[]{"trial", "house", "thought", "quick brown fish", "lazy dogs"};

    protected Supplier<Classifier> factory;

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
    }

    @Test
    public void untrainedClassifierReturnsUnclassified() {

        Classifier classifier = factory.get();

        for (String value : TEST_VALUES) {
            assertEquals(Classification.UNCLASSIFIED, classifier.classify(value));
        }
    }
}
