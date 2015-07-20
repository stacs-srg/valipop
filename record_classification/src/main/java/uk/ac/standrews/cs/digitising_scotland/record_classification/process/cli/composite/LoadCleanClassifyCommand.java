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
import org.apache.commons.csv.CSVFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaners;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Charsets;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.ClassifyCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.CleanGoldStandardCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.LoadDataCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.ClassifyUnseenRecordsStep;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.CleanDataStep;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.LoadDataStep;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Composite command that loads data, cleans and classifies.
 *
 * Example command line invocation:
 *
 * <code>
 *   java uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher
 *   load_clean_classify
 *   -p trained_hisco_classifier
 *   -d unseen_data.csv
 *   -o classified_data.csv
 *   -f JSON_COMPRESSED
 *   -cl COMBINED
 * </code>
 *
 * Or via Maven:
 *
 *   mvn exec:java -q -Dexec.cleanupDaemonThreads=false
 *   -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher" -e
 *   -Dexec.args="load_clean_classify -p trained_hisco_classifier -d unseen_data.csv -o classified_data.csv -f JSON_COMPRESSED -cl COMBINED"
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

        // If charsets are specified, use the last one for the unseen data file.
        Charsets charset = charsets == null ? LoadDataStep.DEFAULT_CHARSET : charsets.get(charsets.size()-1);

        // If delimiters are specified, use the last one for the unseen data file.
        String delimiter = delimiters == null ? LoadDataStep.DEFAULT_DELIMITER : delimiters.get(delimiters.size()-1);

        loadCleanClassify(unseen_data, charset, delimiter, destination, serialization_format, name, process_directory, cleaners);

        return null;
    }

    public static void loadCleanClassify(Path unseen_data, Charsets unseen_data_charsets, String unseen_data_delimiter, Path destination, SerializationFormat serialization_format, String process_name, Path process_directory, List<Cleaners> cleaners) throws Exception {

        System.out.println("loading model...");

//        LoadDataCommand.loadData(unseen_data, unseen_data_charsets, unseen_data_delimiter, serialization_format, process_name, process_directory);
//
//        CleanDataCommand.cleanData(serialization_format, process_name, process_directory, cleaners);
//
//        ClassifyCommand.classify(unseen_data, destination, serialization_format, process_name, process_directory);


        Path serialized_context_path = Serialization.getSerializedContextPath(process_directory,process_name,serialization_format);
        ClassificationContext context = Serialization.loadContext(serialized_context_path, serialization_format);
        context.getClassifier().recoverFromDeserialization();

        System.out.println("loading data...");

        new LoadDataStep(unseen_data, unseen_data_charsets == null ? Charsets.UTF_8.get() : unseen_data_charsets.get(), unseen_data_delimiter == null ? "," : unseen_data_delimiter).perform(context);

        System.out.println("cleaning data...");

        for (Cleaner cleaner : cleaners) {
            new CleanDataStep(cleaner).perform(context);
        }

        System.out.println("classifying data...");

        new ClassifyUnseenRecordsStep().perform(context);

        System.out.println("saving results...");

        final CSVFormat output_format = getDataFormat(",");
        final DataSet classified_data_set = context.getClassifiedUnseenRecords().toDataSet(Arrays.asList("id", "data", "code"), output_format);
        persistDataSet(destination, classified_data_set);

        System.out.println("saving context...");

        context.getClassifier().prepareForSerialization();
        Serialization.persistContext(context, serialized_context_path, serialization_format);
    }

    @Override
    public void perform(ClassificationContext context) {
    }
}
