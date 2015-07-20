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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.composite;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaners;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;

import java.nio.file.Path;
import java.util.List;

/**
 * Composite command that loads data, cleans and classifies.
 *
 * Example command line invocation:
 *
 * <code>
 *   java uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher
 *   load_clean_classify
 *   -d
 *   /Users/graham/Desktop/unseen_data.csv
 *   -o
 *   /Users/graham/Desktop/classified_data.csv
 *   -p
 *   /Users/graham/Desktop/process_state
 *   -f
 *   JSON
 *   -cl
 *   STOP_WORDS
 *   -cl
 *   PORTER_STEM
 *   -cl
 *   CONSISTENT_CLASSIFICATION_CLEANER_CORRECT
 * </code>
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = LoadCleanClassifyCommand.NAME, commandDescription = "Initialise process, load training data, and train classifier")
public class LoadCleanClassifyCommand extends Command {

    /**
     * The name of this command
     */
    public static final String NAME = "load_clean_classify";
    private static final long serialVersionUID = 8026292848547343006L;

    @Parameter(required = true, names = {LoadDataCommand.DATA_FLAG_SHORT, LoadDataCommand.DATA_FLAG_LONG}, description = LoadDataCommand.DATA_DESCRIPTION, converter = PathConverter.class)
    private Path unseen_data;

    @Parameter(required = true, names = {CleanGoldStandardCommand.CLEAN_FLAG_SHORT, CleanGoldStandardCommand.CLEAN_FLAG_LONG}, description = CleanGoldStandardCommand.CLEAN_DESCRIPTION)
    private List<Cleaners> cleaners;

    @Parameter(required = true, names = {ClassifyCommand.DESTINATION_FLAG_SHORT, ClassifyCommand.DESTINATION_FLAG_LONG}, description = ClassifyCommand.DESTINATION_DESCRIPTION, converter = PathConverter.class)
    private Path destination;

    @Override
    public Void call() throws Exception {

        loadCleanClassify(unseen_data, charset, delimiter, destination, serialization_format, name, process_directory, cleaners);

        return null;
    }

    public static void loadCleanClassify(Path unseen_data, Charsets unseen_data_charsets, String unseen_data_delimiter, Path destination, SerializationFormat serialization_format, String process_name, Path process_directory, List<Cleaners> cleaners) throws Exception {

        LoadDataCommand.loadData(unseen_data, unseen_data_charsets, unseen_data_delimiter, serialization_format, process_name, process_directory);

        CleanDataCommand.cleanData(serialization_format, process_name, process_directory, cleaners);

        ClassifyCommand.classify(unseen_data, destination, serialization_format, process_name, process_directory);
    }

    @Override
    public void perform(ClassificationContext context) {
    }
}
