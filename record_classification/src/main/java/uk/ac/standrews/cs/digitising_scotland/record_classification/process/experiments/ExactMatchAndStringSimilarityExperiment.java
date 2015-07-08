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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.experiments;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.StringSimilarityClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.StringSimilarityMetric;

import java.io.IOException;
import java.util.List;

public class ExactMatchAndStringSimilarityExperiment extends Experiment {

    public ExactMatchAndStringSimilarityExperiment() {

    }

    public ExactMatchAndStringSimilarityExperiment(final String[] args) {

        super(args);
    }

    public static void main(final String[] args) throws Exception {

        final ExactMatchAndStringSimilarityExperiment experiment = new ExactMatchAndStringSimilarityExperiment(args);
        experiment.call();
    }

    @Override
    protected List<ClassificationProcess> getClassificationProcesses() throws IOException, InputFileFormatException {

        return getClassificationProcesses(new ExactMatchClassifier(), new StringSimilarityClassifier(StringSimilarityMetric.JARO_WINKLER));
    }
}
