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

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.generic.Experiment;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassifierFactory;

import java.io.*;
import java.util.*;

public class ExactMatchOnlyExperiment extends Experiment {

    public ExactMatchOnlyExperiment() throws IOException, InputFileFormatException {

    }

    public ExactMatchOnlyExperiment(final String[] args) throws IOException, InputFileFormatException {

        super(args);
    }

    public static void main(String[] args) throws Exception {

        final ExactMatchOnlyExperiment experiment = new ExactMatchOnlyExperiment(args);
        experiment.call();
    }

    @Override
    protected List<ClassifierFactory> getClassifierFactories() throws IOException, InputFileFormatException {

        return Arrays.asList(() -> new ExactMatchClassifier());

    }
}
