/*
 * record_classification - Automatic record attribute classification.
 * Copyright © 2014-2016 Digitising Scotland project
 * (http://digitisingscotland.cs.st-andrews.ac.uk/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.specific;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ClassifierSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.generic.Experiment;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ExactMatchOnlyExperiment extends Experiment {

    public ExactMatchOnlyExperiment() throws IOException, InputFileFormatException {

    }

    public ExactMatchOnlyExperiment(final String[] args) throws IOException, InputFileFormatException {

        super(args);
    }

    public static void main(String[] args) throws Exception {

        new ExactMatchOnlyExperiment(args).call();
    }

    @Override
    protected List<Supplier<Classifier>> getClassifierFactories() throws IOException, InputFileFormatException {

        return Collections.singletonList(ClassifierSupplier.EXACT_MATCH);
    }
}
