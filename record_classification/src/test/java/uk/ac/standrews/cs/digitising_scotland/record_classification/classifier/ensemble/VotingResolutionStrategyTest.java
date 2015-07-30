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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ensemble;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenList;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Graham Kirby
 */
@RunWith(Parameterized.class)
public class VotingResolutionStrategyTest {

    private static final double DELTA = 0.001;

    List<Classification> alternative_classifications;
    Classification expected_classification;
    String test_description;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> generateData() {

        List<Object[]> result = new ArrayList<>();

        result.add(single());
        result.add(twoWithDifferentConfidence());
        result.add(three());
        result.add(five());

        return result;
    }

    public VotingResolutionStrategyTest(String test_description,
                                        List<Classification> alternative_classifications,
                                        Classification expected_classification) {

        this.test_description = test_description;
        this.alternative_classifications = alternative_classifications;
        this.expected_classification = expected_classification;
    }

    @Test
    public void checkResults() {

        final Map<Classifier, Classification> candidate_classifications = makeClassificationMap(alternative_classifications);

        final Classification actual_classification = new VotingResolutionStrategy().resolve(candidate_classifications);
        assertEquals(actual_classification.getCode(), expected_classification.getCode());
        assertEquals(actual_classification.getConfidence(), expected_classification.getConfidence(), DELTA);
    }

    private static Object[] single() {

        Classification classification_1 = new Classification("abc", new TokenList("abc"), 0.5);

        //noinspection ArraysAsListWithZeroOrOneArgument
        return new Object[]{"single", Arrays.asList(classification_1), classification_1};
    }

    private static Object[] twoWithDifferentConfidence() {

        Classification classification_1 = new Classification("abc", new TokenList("abc"), 0.5);
        Classification classification_2 = new Classification("def", new TokenList("def"), 0.6);

        return new Object[]{"twoWithDifferentConfidence", Arrays.asList(classification_1, classification_2), classification_2};
    }

    private static Object[] three() {

        Classification classification_1 = new Classification("abc", new TokenList("abc"), 0.5);
        Classification classification_2 = new Classification("def", new TokenList("def"), 0.2);
        Classification classification_3 = new Classification("def", new TokenList("def"), 0.1);

        Classification expected = new Classification("def", new TokenList("def"), 0.15);

        return new Object[]{"three", Arrays.asList(classification_1, classification_2, classification_3), expected};
    }

    private static Object[] five() {

        Classification classification_1 = new Classification("abc", new TokenList("abc"), 0.5);
        Classification classification_2 = new Classification("def", new TokenList("def"), 0.4);
        Classification classification_3 = new Classification("def", new TokenList("def"), 0.1);
        Classification classification_4 = new Classification("ghi", new TokenList("ghi"), 0.3);
        Classification classification_5 = new Classification("ghi", new TokenList("ghi"), 0.3);

        Classification expected = new Classification("def", new TokenList("def"), 0.25);

        return new Object[]{"five", Arrays.asList(classification_1, classification_2, classification_3, classification_4, classification_5), expected};
    }

    private Map<Classifier, Classification> makeClassificationMap(List<Classification> classifications) {

        Map<Classifier, Classification> result = new HashMap<>();
        for (Classification classification : classifications) {
            result.put(makeDummyClassifier(), classification);
        }
        return result;
    }

    private Classifier makeDummyClassifier() {

        return new Classifier() {

            @Override
            public void train(Bucket bucket) {
            }

            @Override
            public Classification classify(String data) {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }
        };
    }
}

