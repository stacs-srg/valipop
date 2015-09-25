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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.naive_bayes;

import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class NaiveBayesClassifierTest {

    //TODO improve tests
    @Test
    public void testSpaceInClassificationCodeDuringTraining() throws Exception {

        final NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        final Bucket gold_standard = new Bucket();

        gold_standard.add(new Record(1, "fish", new Classification("swims", new TokenList("fish"), 1.0, null)));
        gold_standard.add(new Record(2, "fish", new Classification("swims ", new TokenList("fish"), 1.0, null)));

        final Bucket cleaned_gold_standard = new TrimClassificationCodesCleaner().apply(gold_standard);

        classifier.trainModel(cleaned_gold_standard);

    }
}
