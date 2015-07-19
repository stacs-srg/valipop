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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifiers;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;

import java.nio.file.Path;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = InitLoadTrainCommand.NAME, commandDescription = "Initialise process, load training data, and train classifier")
class InitLoadTrainCommand extends Command {

    /**
     * The name of this command
     */
    public static final String NAME = "init_load_train";
    private static final long serialVersionUID = 8026292848547343006L;

    @Parameter(required = true, names = {InitCommand.CLASSIFIER_FLAG_SHORT, InitCommand.CLASSIFIER_FLAG_LONG}, description = InitCommand.CLASSIFIER_DESCRIPTION)
    private Classifiers classifier_supplier;

    @Parameter(required = true, names = {LoadCommand.GOLD_STANDARD_FLAG_SHORT, LoadCommand.GOLD_STANDARD_FLAG_LONG}, description = LoadCommand.GOLD_STANDARD_DESCRIPTION, converter = PathConverter.class)
    private Path gold_standard;

    @Parameter(required = true, names = {TrainCommand.TRAINING_RATIO_FLAG_SHORT, TrainCommand.TRAINING_RATIO_FLAG_LONG}, description = TrainCommand.TRAINING_RATIO_DESCRIPTION)
    private Double training_ratio;

    @Override
    public Void call() throws Exception {

        initLoadTrain(classifier_supplier, gold_standard, training_ratio, serialization_format, name, process_directory);

        return null;
    }

    public static void initLoadTrain(Classifiers classifier_supplier, Path gold_standard, Double training_ratio, SerializationFormat serialization_format, String process_name, Path process_directory) throws Exception {

        Launcher.main(addArgs(
                new String[]{InitCommand.NAME, InitCommand.CLASSIFIER_FLAG_SHORT, classifier_supplier.toString()}, serialization_format, process_name, process_directory));

        Launcher.main(addArgs(
                new String[]{LoadCommand.NAME, LoadCommand.GOLD_STANDARD_FLAG_SHORT, gold_standard.toString()}, serialization_format, process_name, process_directory));

        Launcher.main(addArgs(
                new String[]{TrainCommand.NAME, TrainCommand.TRAINING_RATIO_FLAG_SHORT, String.valueOf(training_ratio)}, serialization_format, process_name, process_directory));
    }

    @Override
    public void perform(ClassificationContext context) {
    }
}
