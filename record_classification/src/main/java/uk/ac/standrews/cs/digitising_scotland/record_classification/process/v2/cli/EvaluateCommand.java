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
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

import java.io.*;

/**
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandDescription = "Evaluate classifier", separators = "=")
public class EvaluateCommand {

    @Parameter(required = true, names = {"-d", "--destination"}, description = "Path to the place to persist the evaluation results.", converter = CommandLineUtils.FileConverter.class)
    private File destination;

    @Parameter(names = {"-d", "--destination"}, description = "The number of repetitions.")
    private int repetition_count = 1;

    public File getDestination() {

        return destination;
    }

    public int getRepetitionCount() {

        return repetition_count;
    }
}
