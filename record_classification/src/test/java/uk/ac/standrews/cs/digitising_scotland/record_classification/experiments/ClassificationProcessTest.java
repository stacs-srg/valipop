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
package uk.ac.standrews.cs.digitising_scotland.record_classification.experiments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Classifiers;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassifierFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ClassificationProcessTest extends AbstractClassificationProcessTest {

    private final Classifier classifier;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> generateData() {

        List<Object[]> result = new ArrayList<>();

        result.add(new Object[]{Classifiers.STRING_SIMILARITY_DICE});
        result.add(new Object[]{Classifiers.STRING_SIMILARITY_JACCARD});
        result.add(new Object[]{Classifiers.EXACT_MATCH_PLUS_STRING_SIMILARITY_JARO_WINKLER});
        result.add(new Object[]{Classifiers.EXACT_MATCH_PLUS_STRING_SIMILARITY_LEVENSHTEIN});
        result.add(new Object[]{Classifiers.OLR});

        return result;
    }

    public ClassificationProcessTest(Classifier classifier) {

        this.classifier = classifier;
    }

    protected ClassifierFactory getClassifierFactory() {

        return () -> classifier;
    }

    /**
     * This just checks that the process of classification and evaluation runs without errors.
     *
     * @throws Exception
     */
    @Test
    public void checkProcessRunsWithoutErrors() throws Exception {

    }
}
