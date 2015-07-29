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
package uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.specific;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ClassifierSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.generic.Experiment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class VariousClassifiersExperiment extends Experiment {

    protected VariousClassifiersExperiment(final String[] args) throws IOException, InputFileFormatException {

        super(args);
    }

    public static void main(final String[] args) throws Exception {

        final VariousClassifiersExperiment experiment = new VariousClassifiersExperiment(args);
        experiment.call();
    }

    @Override
    protected List<Supplier<Classifier>> getClassifierFactories() throws IOException, InputFileFormatException {
        
        return Arrays.asList(ClassifierSupplier.values());
//                ClassifierSupplier.EXACT_MATCH,
//                ClassifierSupplier.STRING_SIMILARITY_LEVENSHTEIN,
//                ClassifierSupplier.STRING_SIMILARITY_JARO_WINKLER,
//                ClassifierSupplier.STRING_SIMILARITY_JACCARD,
//                ClassifierSupplier.STRING_SIMILARITY_DICE,
//                ClassifierSupplier.OLR,
//                ClassifierSupplier.EXACT_MATCH_PLUS_STRING_SIMILARITY_LEVENSHTEIN,
//                ClassifierSupplier.EXACT_MATCH_PLUS_STRING_SIMILARITY_JARO_WINKLER,
//                () -> Classifiers.EXACT_MATCH_PLUS_STRING_SIMILARITY_JACCARD,
//                () -> Classifiers.EXACT_MATCH_PLUS_STRING_SIMILARITY_CHAPMAN_LENGTH_DEVIATION,
//                () -> Classifiers.EXACT_MATCH_PLUS_STRING_SIMILARITY_DICE,
//                () -> ClassifierSupplier.EXACT_MATCH_PLUS_OLR.get())
//                () -> Classifiers.VOTING_ENSEMBLE);
    }
}
