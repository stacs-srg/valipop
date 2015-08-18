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
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ClassifierSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.CleanerSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.CharsetSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.CleanGoldStandardCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.InitCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.LoadGoldStandardCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.TrainCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;

import java.nio.file.Path;
import java.util.List;

/**
 * Composite command that initialises, loads gold standard, cleans and trains.
 * <p/>
 * Example command line invocation:
 * <p/>
 * <code>
 * java uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher
 * init_load_clean_train
 * -g cambridge.csv
 * -g hisco.csv
 * -p trained_hisco_classifier
 * -c EXACT_MATCH_PLUS_VOTING_ENSEMBLE
 * -r 1.0
 * -f JSON_COMPRESSED
 * -cl COMBINED
 * </code>
 * <p/>
 * Or via Maven:
 * <p/>
 * mvn exec:java -q -Dexec.cleanupDaemonThreads=false
 * -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher" -e
 * -Dexec.args="init_load_clean_train -g cambridge.csv -g hisco.csv -p trained_hisco_classifier
 * -c EXACT_MATCH_PLUS_VOTING_ENSEMBLE -r 1.0 -f JSON_COMPRESSED -cl COMBINED"
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = InitLoadCleanTrainCommand.NAME, commandDescription = "Initialise process, load and clean training data, train classifier")
public class InitLoadCleanTrainCommand extends Command {

    /**
     * The name of this command
     */
    public static final String NAME = "init_load_clean_train";
    private static final long serialVersionUID = 8026292848547343006L;

    @Parameter(required = true, names = {InitCommand.CLASSIFIER_FLAG_SHORT, InitCommand.CLASSIFIER_FLAG_LONG}, description = InitCommand.CLASSIFIER_DESCRIPTION)
    private ClassifierSupplier classifier_supplier;

    @Parameter(required = true, names = {LoadGoldStandardCommand.GOLD_STANDARD_FLAG_SHORT, LoadGoldStandardCommand.GOLD_STANDARD_FLAG_LONG}, description = LoadGoldStandardCommand.GOLD_STANDARD_DESCRIPTION, converter = PathConverter.class)
    private List<Path> gold_standards;

    @Parameter(required = true, names = {LoadGoldStandardCommand.TRAINING_RATIO_FLAG_SHORT, LoadGoldStandardCommand.TRAINING_RATIO_FLAG_LONG}, description = LoadGoldStandardCommand.TRAINING_RATIO_DESCRIPTION)
    private List<Double> training_ratios;

    @Parameter(required = true, names = {CleanGoldStandardCommand.CLEAN_FLAG_SHORT, CleanGoldStandardCommand.CLEAN_FLAG_LONG}, description = CleanGoldStandardCommand.CLEAN_DESCRIPTION)
    private List<CleanerSupplier> cleaners;

    @Override
    public Void call() throws Exception {

        initLoadCleanTrainUsingAPI(classifier_supplier, gold_standards, charsets, delimiters, training_ratios, serialization_format, name, process_directory, cleaners);

        return null;
    }

    public static void initLoadCleanTrain(ClassifierSupplier classifier_supplier, List<Path> gold_standard, List<CharsetSupplier> charsets, List<String> delimiters, List<Double> training_ratios, SerializationFormat serialization_format, String process_name, Path process_directory, List<CleanerSupplier> cleaners, boolean use_cli) throws Exception {

        if (use_cli) {
            initLoadCleanTrainUsingCLI(classifier_supplier, gold_standard, charsets, delimiters, training_ratios, serialization_format, process_name, process_directory, cleaners);
        } else {
            initLoadCleanTrainUsingAPI(classifier_supplier, gold_standard, charsets, delimiters, training_ratios, serialization_format, process_name, process_directory, cleaners);
        }
    }

    public static void initLoadCleanTrainUsingAPI(ClassifierSupplier classifier_supplier, List<Path> gold_standards, List<CharsetSupplier> charsets, List<String> delimiters, List<Double> training_ratios, SerializationFormat serialization_format, String process_name, Path process_directory, List<CleanerSupplier> cleaners) throws Exception {

        ClassificationContext context = InitCommand.perform(classifier_supplier, process_directory, process_name);

        LoadGoldStandardCommand.perform(context, gold_standards, training_ratios, charsets, delimiters);
        CleanGoldStandardCommand.perform(context, cleaners);
        TrainCommand.performCommand(context);

        Serialization.persistContext(context, process_directory, process_name, serialization_format);
    }

    public static void initLoadCleanTrainUsingCLI(ClassifierSupplier classifier_supplier, List<Path> gold_standard, List<CharsetSupplier> charsets, List<String> delimiters, List<Double> training_ratios, SerializationFormat serialization_format, String process_name, Path process_directory, List<CleanerSupplier> cleaners) throws Exception {

        InitCommand.perform(serialization_format, process_name, process_directory, classifier_supplier);
        LoadGoldStandardCommand.perform(serialization_format, process_name, process_directory, gold_standard, training_ratios, charsets, delimiters);
        CleanGoldStandardCommand.perform(serialization_format, process_name, process_directory, cleaners);
        TrainCommand.perform(serialization_format, process_name, process_directory);
    }

    @Override
    public void perform(ClassificationContext context) {
    }
}
