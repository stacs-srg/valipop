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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import com.beust.jcommander.*;
import com.beust.jcommander.converters.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.nio.charset.*;
import java.nio.file.*;
import java.util.logging.*;

/**
 * Sets a variable in classification process
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = SetCommand.NAME, commandDescription = "Sets a variable in classification process")
public class SetCommand extends Command {

    private static final Logger LOGGER = Logger.getLogger(SetCommand.class.getName());

    /** The name of this command. */
    public static final String NAME = "set";

    @Parameter(names = "classifier", description = "The classifier to use for the classification process.")
    private ClassifierSupplier classifier_supplier;

    @Parameter(names = "seed", description = "The seed of random number generator.")
    private Long seed;

    @Parameter(names = "charset", description = "The default charset of input/output files.")
    private CharsetSupplier charset_supplier;

    @Parameter(names = "delimiter", description = "The default delimiter of input/output files.")
    private Character delimiter;

    @Parameter(names = "serialization_format", description = "The format of serialised internal classification process settings.")
    private SerializationFormat serialization_format;

    @Parameter(names = "working_directory", description = "The path to the working directory.", converter = PathConverter.class)
    private Path working_directory;

    public SetCommand(final Launcher launcher) { super(launcher); }

    @Override
    public void run() {

        final ClassificationContext context = launcher.getContext();
        final Configuration configuration = launcher.getConfiguration();

        if (classifier_supplier != null) {
            final Classifier classifier = classifier_supplier.get();
            LOGGER.info(() -> "Setting classifier to " + classifier_supplier);
            configuration.setClassifier(classifier);
        }

        if (seed != null) {
            LOGGER.info(() -> "Setting seed to " + seed);
            configuration.setSeed(seed);
        }

        if (charset_supplier != null) {
            LOGGER.info(() -> "Setting default charset to " + charset_supplier);
            configuration.setDefaultCharsetSupplier(charset_supplier);
        }

        if (delimiter != null) {
            LOGGER.info(() -> "Setting default delimiter to " + delimiter);
            configuration.setDefaultDelimiter(delimiter);
        }

        if (serialization_format != null) {
            LOGGER.info(() -> "Setting serialization format to " + serialization_format);
            configuration.setSerializationFormat(serialization_format);
        }

        if (working_directory != null) {
            LOGGER.info(() -> "Setting the working directory to " + working_directory);
            configuration.setWorkingDirectory(working_directory);
        }
    }
}
