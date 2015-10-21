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
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.time.*;
import java.util.*;
import java.util.logging.*;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = TrainCommand.NAME, commandDescription = "Train classifier")
public class TrainCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "train";

    private static final Logger LOGGER = Logger.getLogger(TrainCommand.class.getName());

    @Parameter(names = {SetCommand.OPTION_INTERNAL_TRAINING_RATIO_SHORT, SetCommand.OPTION_INTERNAL_TRAINING_RATIO_LONG},
                    description = "The ratio of gold standard records to be used for training as opposed to internal evaluation. The value must be between 0.0 to 1.0 (inclusive).",
                    validateValueWith = Validators.BetweenZeroToOneInclusive.class)
    private Double internal_training_ratio = launcher.getConfiguration().getDefaultInternalTrainingRatio();

    /**
     * Instantiates this command for the given launcher.
     *
     * @param launcher the launcher to which this command belongs.
     */
    public TrainCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();
        final Classifier classifier = configuration.requireClassifier();

        final Bucket training_records = configuration.requireTrainingRecords();

        final Instant start = Instant.now();
        classifier.trainAndEvaluate(training_records, internal_training_ratio, configuration.getRandom());
        final Duration training_time = Duration.between(start, Instant.now());

        LOGGER.info(() -> String.format("trained the classifier on %d in %s", training_records.size(), training_time));
    }
}
