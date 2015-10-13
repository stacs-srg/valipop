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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.TrainClassifierStep;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = TrainCommand.NAME, commandDescription = "Train classifier")
public class TrainCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "train";

    public static final double DEFAULT_INTERNAL_TRAINING_RATIO = 0.8;

    public static final String INTERNAL_TRAINING_RATIO_DESCRIPTION = "The ratio of gold standard records to be used for training as opposed to internal evaluation.";
    public static final String INTERNAL_TRAINING_RATIO_FLAG_SHORT = "-ir";
    public static final String INTERNAL_TRAINING_RATIO_FLAG_LONG = "--internalTrainingRecordRatio";

    @Parameter(names = {INTERNAL_TRAINING_RATIO_FLAG_SHORT, INTERNAL_TRAINING_RATIO_FLAG_LONG}, description = INTERNAL_TRAINING_RATIO_DESCRIPTION, validateValueWith = Validators.BetweenZeroAndOne.class)
    private Double internal_training_ratio = DEFAULT_INTERNAL_TRAINING_RATIO;

    public TrainCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final ClassificationContext context = launcher.getContext();
        new TrainClassifierStep(internal_training_ratio).perform(context);
    }
}
