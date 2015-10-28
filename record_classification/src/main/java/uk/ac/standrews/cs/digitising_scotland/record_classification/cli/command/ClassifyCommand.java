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
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

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

    private static final Logger LOGGER = getLogger(ClassifyCommand.class.getName());

    @Parameter(required = true, names = {OPTION_OUTPUT_RECORDS_PATH_SHORT, OPTION_OUTPUT_RECORDS_PATH_LONG}, descriptionKey = "command.classify.output.description", converter = PathConverter.class)
    private Path output_path;

    /**
     * Instantiates this command for the given launcher.
     *
     * @param launcher the launcher to which this format belongs.
     */
    public ClassifyCommand(final Launcher launcher) { super(launcher, NAME); }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();
        final Classifier classifier = configuration.requireClassifier();

        final List<Configuration.Unseen> unseens = configuration.getUnseens();
        final List<Bucket> unseen_records_list = configuration.requireUnseenRecordsList();
        final List<Bucket> classified_unseen_records_list = unseen_records_list.stream().map(classifier::classify).collect(Collectors.toList());

        for (int index = 0; index < unseen_records_list.size(); index++) {
            final Configuration.Unseen unseen = unseens.get(index);
            final Bucket classified_unseen_records = classified_unseen_records_list.get(index);
            unseen.setBucket(classified_unseen_records);
        }

        persistClassifiedUnseenRecords(classified_unseen_records_list);
    }

    private void persistClassifiedUnseenRecords(final List<Bucket> classified_unseen_records_list) {

        final Bucket classified_unseen_records = classified_unseen_records_list.stream().reduce(Bucket::union).orElse(new Bucket());
        LOGGER.info(() -> String.format("Persisting total of %d classified unseen records into path: %s", classified_unseen_records.size(), output_path));

        try {
            persistBucketAsCSV(classified_unseen_records, output_path, Configuration.RECORD_CSV_FORMAT, Configuration.RESOURCE_CHARSET);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to persist classified unseen records: " + e.getMessage(), e);
            throw new IOError(e);
        }
    }
}
