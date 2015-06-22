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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.cli;

import com.beust.jcommander.*;
import org.apache.commons.lang3.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.CommandLineUtils.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

/**
 * Launches an exact match classification process.
 *
 * @author Masih Hajiarab Derkani
 */
public class ExactMatchClassificationProcessLauncher {

    @Parameter(required = true, names = {"-g", "--goldStandard"}, description = "Path to a CSV file containing the gold standard.", converter = FileConverter.class)
    private File gold_standard_csv;

    @Parameter(required = true, names = {"-r", "--trainingRecordRatio"}, description = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).")
    private Double training_ratio;

    @Parameter(required = true, names = {"-d", "--destination"}, description = "Path to save the classification process.", converter = FileConverter.class)
    private File persistence_destination;

    @Parameter(names = {"-c", "--cleanGoldStandard"}, description = "The name of the gold_standard_cleaner by which to clean the gold standard data prior to training/evaluation. May be one of: [NONE, CHECK, REMOVE, CORRECT]", converter = CleanerConverter.class)
    private ConsistentCodingCleaner gold_standard_cleaner = ConsistentCodingCleaner.CORRECT;

    @Parameter(names = {"-e", "--evaluate"}, description = "weather to evaluate the classifier after training with gold standard")
    private boolean evaluate = false;

    public static void main(String[] args) throws Exception {

        final ExactMatchClassificationProcessLauncher process = new ExactMatchClassificationProcessLauncher();
        final JCommander commander = new JCommander(process);

        try {
            commander.parse(args);
        }
        catch (ParameterException e) {
            System.err.println(e.getMessage());
            commander.usage();
            System.exit(1);
        }

        process.perform();
    }

    private void perform() throws Exception {

        final Context context = new Context();
        context.setClassifier(new ExactMatchClassifier());

        final ClassificationProcess classification_process = new ClassificationProcess(context);
        classification_process.addStep(new LoadGoldStandardFromCSVFile(gold_standard_csv, Charset.defaultCharset()));
        classification_process.addStep(new CleanGoldStandardRecords(gold_standard_cleaner));
        classification_process.addStep(new SetTrainingRecordsByRatio(training_ratio));
        classification_process.addStep(new TrainClassifier());

        if (evaluate) {
            classification_process.addStep(new EvaluateClassifier());
        }

        classification_process.call();

        if (persistence_destination.exists()) {
            throw new ParameterException("destination already exists.");
        }

        final byte[] serialized_process = SerializationUtils.serialize(classification_process);
        Files.write(persistence_destination.toPath(), serialized_process);
    }
}
