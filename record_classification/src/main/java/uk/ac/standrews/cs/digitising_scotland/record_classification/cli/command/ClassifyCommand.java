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
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.logging.*;

import static java.util.logging.Logger.getLogger;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.Configuration.persistBucketAsCSV;

/**
 * Classifies unseen records.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = ClassifyCommand.NAME, resourceBundle = Configuration.RESOURCE_BUNDLE_NAME, commandDescriptionKey = "command.classify.description")
public class ClassifyCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "classify";

    /** The short name of the option that specifies the path in which to store the classified records. **/
    public static final String OPTION_OUTPUT_RECORDS_PATH_SHORT = "-o";

    /** The long name of the option that specifies the path in which to store the classified records. **/
    public static final String OPTION_OUTPUT_RECORDS_PATH_LONG = "--output";

    @Parameter(required = true, names = {OPTION_OUTPUT_RECORDS_PATH_SHORT, OPTION_OUTPUT_RECORDS_PATH_LONG}, descriptionKey = "command.classify.output.description", converter = PathConverter.class)
    private Path output_path;

    public static class Builder extends Command.Builder {

        private Path output_path;

        public Builder output(Path output_path) {

            this.output_path = output_path;
            return this;
        }

        @Override
        public String[] build() {

            Objects.requireNonNull(output_path);

            final List<String> arguments = new ArrayList<>();
            arguments.add(NAME);
            arguments.add(OPTION_OUTPUT_RECORDS_PATH_SHORT);
            arguments.add(output_path.toString());

            return arguments.toArray(new String[arguments.size()]);
        }
    }

    /**
     * Instantiates this command for the given launcher.
     *
     * @param launcher the launcher to which this format belongs.
     */
    public ClassifyCommand(final Launcher launcher) { super(launcher, NAME); }

    @Override
    public void run() {

        final Classifier classifier = configuration.requireClassifier();

        final Bucket unseen_records = configuration.requireUnseenRecords();
        final Instant start = Instant.now();
        final Bucket classified_unseen_records = classifier.classify(unseen_records);
        final Duration classification_time = Duration.between(start, Instant.now());

        configuration.setClassifiedUnseenRecords(classified_unseen_records);
        logger.info(() -> String.format("classified %d records in %s", classified_unseen_records.size(), classification_time));

        persistClassifiedUnseenRecords(classified_unseen_records);
    }

    private void persistClassifiedUnseenRecords(final Bucket classified_unseen_records) {

        final Path destination = resolveRelativeToWorkingDirectory(output_path);

        logger.info(() -> String.format("Persisting total of %d classified unseen records into path: %s", classified_unseen_records.size(), destination));
        try {
            persistBucketAsCSV(classified_unseen_records, destination, Configuration.RECORD_CSV_FORMAT, Configuration.RESOURCE_CHARSET);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "failed to persist classified unseen records: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
