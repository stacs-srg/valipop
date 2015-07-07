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

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.EnsembleVotingClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.StringSimilarityClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.StringSimilarityMetric;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EnsembleOnlyExperiment extends Experiment {

    protected EnsembleOnlyExperiment(final String[] args) {

        super(args);
    }

    public static void main(final String[] args) throws Exception {

        final EnsembleOnlyExperiment experiment = new EnsembleOnlyExperiment(args);
        experiment.call();
    }

    @Override
    protected List<ClassificationProcess> initClassificationProcesses() throws IOException, InputFileFormatException {

        return initClassificationProcesses(new EnsembleVotingClassifier(Arrays.asList(new StringSimilarityClassifier(StringSimilarityMetric.LEVENSHTEIN), new StringSimilarityClassifier(StringSimilarityMetric.JARO_WINKLER))));
    }
}
