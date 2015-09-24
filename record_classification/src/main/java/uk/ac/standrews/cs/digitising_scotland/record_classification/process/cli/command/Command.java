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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.command;

import com.beust.jcommander.Parameter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.*;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.util.tools.Logging;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.LoadStep;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * The glue-code connecting {@link Step steps} in a {@link ClassificationProcess classification process} to the functionality exposed by the command-line interface.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public abstract class Command implements Runnable {

    public static final String CLEAN_DESCRIPTION = "A cleaner with which to clean the data";
    public static final String CLEAN_FLAG_SHORT = "-cl";
    public static final String CLEAN_FLAG_LONG = "--cleaner";

    protected static final List<String> DATA_SET_COLUMN_LABELS = Arrays.asList("id", "data", "code", "confidence", "classification_details");

    private static final Charset DEFAULT_CHARSET = LoadStep.DEFAULT_CHARSET_SUPPLIER.get();
    public static final String DEFAULT_DELIMITER = LoadStep.DEFAULT_DELIMITER;

    protected final Launcher launcher;

    public Command(Launcher launcher) {

        this.launcher = launcher;
    }

    protected static void output(String message) {

        Logging.output(InfoLevel.VERBOSE, message);
    }
}
