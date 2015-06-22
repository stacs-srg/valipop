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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.cli;

import com.beust.jcommander.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

/**
 * Initialisation command of the classification process.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandDescription = "Initialise a new classification process", separators = "=")
public class InitCommand {

    @Parameter(required = true, description = "The name of the classification process.")
    private String name;

    @Parameter(required = true, names = {"-c", "--classifier"}, description = "The classifier to use for classification process.", converter = CommandLineUtils.ClassifierConverter.class)
    private Classifier classifier;

    /**
     * Gets the name of initialised classification process
     *
     * @return the name of initialised classification process
     */
    public String getName() {

        return name;
    }

    /**
     * Gets the classifier.
     *
     * @return the classifier
     */
    public Classifier getClassifier() {

        return classifier;
    }
}
