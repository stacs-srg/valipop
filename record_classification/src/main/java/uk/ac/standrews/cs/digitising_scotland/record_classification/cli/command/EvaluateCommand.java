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
import com.beust.jcommander.converters.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;

import static java.util.logging.Logger.*;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.Configuration.RECORD_CSV_FORMAT;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.Configuration.RESOURCE_CHARSET;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.Configuration.persistBucketAsCSV;

/**
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = EvaluateCommand.NAME, resourceBundle = Configuration.RESOURCE_BUNDLE_NAME, commandDescriptionKey = "command.evaluate.description")
public class EvaluateCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "evaluate";

    /** The short name of the option that specifies the path in which to store the classified evaluation records. **/
    public static final String OPTION_OUTPUT_RECORDS_PATH_SHORT = "-o";

    /** The long name of the option that specifies the path in which to store the classified evaluation records. **/
    public static final String OPTION_OUTPUT_RECORDS_PATH_LONG = "--output";

    @Parameter(names = {OPTION_OUTPUT_RECORDS_PATH_SHORT, OPTION_OUTPUT_RECORDS_PATH_LONG}, description = "command.evaluate.output.description", converter = PathConverter.class)
    private Path classified_evaluation_records;

    public static class Builder extends Command.Builder {

        private Path classified_evaluation_records;

        public Builder output(Path classified_evaluation_records) {

            this.classified_evaluation_records = classified_evaluation_records;
            return this;
        }

        @Override
        public String[] build() {

            final List<String> arguments = new ArrayList<>();
            arguments.add(NAME);
            if (classified_evaluation_records != null) {
                arguments.add(OPTION_OUTPUT_RECORDS_PATH_SHORT);
                arguments.add(classified_evaluation_records.toString());
            }

            return arguments.toArray(new String[arguments.size()]);
        }
    }

    public EvaluateCommand(final Launcher launcher) {

        super(launcher, NAME);
    }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();

        final Bucket evaluation_records = configuration.requireEvaluationRecords();
        final Bucket gold_standard_records = configuration.requireGoldStandardRecords();
        final Bucket evaluation_records_stripped = evaluation_records.stripRecordClassifications();

        final Instant start = Instant.now();
        final Classifier classifier = configuration.requireClassifier();
        final Bucket classified_records = classifier.classify(evaluation_records_stripped);
        final Duration classification_time = Duration.between(start, Instant.now());

        logger.info(() -> String.format("Classified evaluation %d records in %s", evaluation_records_stripped.size(), classification_time));

        final ConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_records, gold_standard_records, new ConsistentCodingChecker());
        final ClassificationMetrics classification_metrics = new ClassificationMetrics(confusion_matrix);

        logConfusionMatrix(confusion_matrix);
        logClassificationMetrics(classification_metrics);

        if (isOutputClassifiedRecordsPathSet()) {
            persistClassifiedEvaluationRecords(classified_records);
        }

        //TODO export matrix as json?
        //TODO export metrics as json?
    }

    private boolean isOutputClassifiedRecordsPathSet() {

        return classified_evaluation_records != null;
    }

    private void persistClassifiedEvaluationRecords(final Bucket classified_records) {

        try {
            persistBucketAsCSV(classified_records, classified_evaluation_records, RECORD_CSV_FORMAT, RESOURCE_CHARSET);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Failure while exporting classified evaluation records: " + e.getMessage(), e);
            throw new IOError(e);
        }
    }

    private void logClassificationMetrics(final ClassificationMetrics classification_metrics) {

        logger.info(() -> String.format("Macro Average Accuracy: %10.2f", classification_metrics.getMacroAverageAccuracy()));
        logger.info(() -> String.format("Macro Average F1: %10.2f", classification_metrics.getMacroAverageF1()));
        logger.info(() -> String.format("Macro Average Precision: %10.2f", classification_metrics.getMacroAveragePrecision()));
        logger.info(() -> String.format("Macro Average Recall: %10.2f", classification_metrics.getMacroAverageRecall()));
        logger.info(() -> String.format("Micro Average Accuracy: %10.2f", classification_metrics.getMicroAverageAccuracy()));
        logger.info(() -> String.format("Micro Average F1: %10.2f", classification_metrics.getMicroAverageF1()));
        logger.info(() -> String.format("Micro Average Precision: %10.2f", classification_metrics.getMicroAveragePrecision()));
        logger.info(() -> String.format("Micro Average Recall: %10.2f", classification_metrics.getMicroAverageRecall()));
    }

    private void logConfusionMatrix(final ConfusionMatrix confusion_matrix) {

        logger.info(() -> String.format("Number Of Classes: %10d", confusion_matrix.getNumberOfClasses()));
        logger.info(() -> String.format("Number Of Classifications: %10d", confusion_matrix.getNumberOfClassifications()));
        logger.info(() -> String.format("Number Of True Positives: %10d", confusion_matrix.getNumberOfTruePositives()));
        logger.info(() -> String.format("Number Of True Negatives: %10d", confusion_matrix.getNumberOfTrueNegatives()));
        logger.info(() -> String.format("Number Of False Negatives: %10d", confusion_matrix.getNumberOfFalseNegatives()));
        logger.info(() -> String.format("Number Of False Positives: %10d", confusion_matrix.getNumberOfFalsePositives()));
    }
}
