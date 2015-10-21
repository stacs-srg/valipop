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
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.time.*;
import java.util.*;
import java.util.logging.*;

import static java.util.logging.Logger.getLogger;

/**
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = EvaluateCommand.NAME, commandDescription = "Evaluate classifier")
public class EvaluateCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "evaluate";

    private static final Logger LOGGER = getLogger(EvaluateCommand.class.getName());

    public EvaluateCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();

        final Optional<Bucket> evaluation_records = configuration.getEvaluationRecords();

        if (!evaluation_records.isPresent()) {
            throw new ParameterException("no evaluation record is present.");
        }

        final Optional<Bucket> gold_standard_records = configuration.getGoldStandardRecords();

        if (!gold_standard_records.isPresent()) {
            throw new ParameterException("no gold standard record is present.");
        }

        final Bucket evaluation_records_stripped = evaluation_records.get().stripRecordClassifications();

        final Instant start = Instant.now();
        final Bucket classified_records = configuration.getClassifier().classify(evaluation_records_stripped);
        final Duration classification_time = Duration.between(start, Instant.now());

        LOGGER.info(() -> String.format("Classified evaluation %d records in %s", evaluation_records_stripped.size(), classification_time));

        final StrictConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_records, gold_standard_records.get(), new ConsistentCodingChecker());
        final ClassificationMetrics classification_metrics = new ClassificationMetrics(confusion_matrix);

        LOGGER.info(() -> String.format("Number Of Classes: %10d", confusion_matrix.getNumberOfClasses()));
        LOGGER.info(() -> String.format("Number Of Classifications: %10d", confusion_matrix.getNumberOfClassifications()));
        LOGGER.info(() -> String.format("Number Of True Positives: %10d", confusion_matrix.getNumberOfTruePositives()));
        LOGGER.info(() -> String.format("Number Of True Negatives: %10d", confusion_matrix.getNumberOfTrueNegatives()));
        LOGGER.info(() -> String.format("Number Of False Negatives: %10d", confusion_matrix.getNumberOfFalseNegatives()));
        LOGGER.info(() -> String.format("Number Of False Positives: %10d", confusion_matrix.getNumberOfFalsePositives()));
        LOGGER.info(() -> String.format("Macro Average Accuracy: %10.2f", classification_metrics.getMacroAverageAccuracy()));
        LOGGER.info(() -> String.format("Macro Average F1: %10.2f", classification_metrics.getMacroAverageF1()));
        LOGGER.info(() -> String.format("Macro Average Precision: %10.2f", classification_metrics.getMacroAveragePrecision()));
        LOGGER.info(() -> String.format("Macro Average Recall: %10.2f", classification_metrics.getMacroAverageRecall()));
        LOGGER.info(() -> String.format("Micro Average Accuracy: %10.2f", classification_metrics.getMicroAverageAccuracy()));
        LOGGER.info(() -> String.format("Micro Average F1: %10.2f", classification_metrics.getMicroAverageF1()));
        LOGGER.info(() -> String.format("Micro Average Precision: %10.2f", classification_metrics.getMicroAveragePrecision()));
        LOGGER.info(() -> String.format("Micro Average Recall: %10.2f", classification_metrics.getMicroAverageRecall()));

        //TODO export matrix as json
        //TODO export metrics as json
        //TODO export classified evaluation records
        //TODO export classified evaluation records
    }
}
