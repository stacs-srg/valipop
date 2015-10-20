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
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.EvaluateClassifierStep;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.SaveDataStep;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.nio.file.Path;
import java.time.*;
import java.util.*;

/**
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = EvaluateCommand.NAME, commandDescription = "Evaluate classifier")
public class EvaluateCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "evaluate";

    @Parameter(required = true, names = {"-o", "--output"}, description = "Path to the place to persist the evaluation results.", converter = PathConverter.class)
    private Path destination;

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

        final Instant start = Instant.now();
        final Bucket classified_records = configuration.getClassifier().classify(evaluation_records.get().makeStrippedRecords());
        final Duration classification_time = Duration.between(start, Instant.now());

        final StrictConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_records, gold_standard_records.get(), new ConsistentCodingChecker());
        final ClassificationMetrics classification_metrics = new ClassificationMetrics(confusion_matrix);

        //TODO log outcomes
    }
}
