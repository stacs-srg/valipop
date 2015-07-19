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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.AddTrainingAndEvaluationRecordsByRatioStep;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.TrainClassifierStep;

import java.nio.file.Path;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = TrainCommand.NAME, commandDescription = "Train classifier")
public class TrainCommand extends Command {

    /** The name of this command */
    public static final String NAME = "train";

    private static final long serialVersionUID = 8026292848547343006L;

    public static final String TRAINING_RATIO_DESCRIPTION = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).";
    public static final String TRAINING_RATIO_FLAG_SHORT = "-r";
    public static final String TRAINING_RATIO_FLAG_LONG = "--trainingRecordRatio";

    @Parameter(required = true, names = {TRAINING_RATIO_FLAG_SHORT, TRAINING_RATIO_FLAG_LONG}, description = TRAINING_RATIO_DESCRIPTION)
    private Double training_ratio;

    @Override
    public void perform(final ClassificationContext context)  {

        new AddTrainingAndEvaluationRecordsByRatioStep(training_ratio).perform(context);
        new TrainClassifierStep().perform(context);
    }

    public static void train(double training_ratio, SerializationFormat serialization_format, String process_name, Path process_directory) throws Exception {

        Launcher.main(addArgs(
                new String[]{NAME, TRAINING_RATIO_FLAG_SHORT, String.valueOf(training_ratio)}, serialization_format, process_name, process_directory));
    }
}
