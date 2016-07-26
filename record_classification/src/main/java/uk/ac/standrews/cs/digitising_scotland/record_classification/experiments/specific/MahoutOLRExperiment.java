/*
 * record_classification - Automatic record attribute classification.
 * Copyright © 2012-2016 Digitising Scotland project
 * (http://digitisingscotland.cs.st-andrews.ac.uk/record_classification/)
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

import com.beust.jcommander.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.logistic_regression.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.generic.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class MahoutOLRExperiment extends Experiment {

    @Parameter(names = {"-f", "--folds"}, description = "Number of folds in cross fold OLR.")
    private int folds = 4;

    @Parameter(names = {"-p", "--passes"}, description = "Number of passes over training records.")
    private int passes = 30;

    protected MahoutOLRExperiment(final String[] args) throws IOException, InputFileFormatException {

        super(args);
    }

    public static void main(final String[] args) throws Exception {

        final MahoutOLRExperiment experiment = new MahoutOLRExperiment(args);
        experiment.call();
    }

    @Override
    protected List<Supplier<Classifier>> getClassifierFactories() throws IOException, InputFileFormatException {

        return Collections.singletonList(() -> new OLRClassifier(folds, passes));
    }
}
