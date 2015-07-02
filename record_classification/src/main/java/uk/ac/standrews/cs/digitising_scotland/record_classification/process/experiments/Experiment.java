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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.experiments;

import com.beust.jcommander.*;
import com.beust.jcommander.converters.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.*;
import uk.ac.standrews.cs.util.dataset.*;
import uk.ac.standrews.cs.util.tables.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public abstract class Experiment implements Callable<Void> {

    protected static final int SEED = 1413;
    protected static final List<Boolean> COLUMNS_AS_PERCENTAGES = Arrays.asList(true, true, true, true, true, true, true, true, false, false);

    private final JCommander commander;

    @Parameter(names = {"-v", "--verbosity"}, description = "The level of output verbosity.")
    protected InfoLevel verbosity = InfoLevel.LONG_SUMMARY;

    @Parameter(names = {"-r", "--repetitionCount"}, description = "The number of repetitions.")
    protected int repetitions = 2;

    @Parameter(names = {"-g", "--goldStandard"}, description = "Path to a file containing the three column gold standard.", converter = FileConverter.class)
    protected File gold_standard = new File("src/test/resources/uk/ac/standrews/cs/digitising_scotland/record_classification/process/experiments/AbstractClassificationTest/gold_standard_small.csv");

    @Parameter(names = {"-t", "--trainingRecordRatio"}, description = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).")
    protected Double training_ratio = 0.8;

    @Parameter(names = {"-d", "--delimiter"}, description = "The delimiter character of three column gold standard data.")
    protected char delimiter = '|';

    protected Experiment(String[] args) {

        commander = new JCommander(this);
        try {
            commander.parse(args);
        }
        catch (ParameterException e) {
            exitWithErrorMessage(e.getMessage());
        }
    }

    @Override
    public Void call() throws Exception {

        final List<String> row_labels = new ArrayList<>();
        final List<DataSet> result_sets = new ArrayList<>();
        final List<ClassificationProcess> processes = getClassificationProcesses();

        for (ClassificationProcess process : processes) {

            final String name = getProcessName(process);
            row_labels.add(name);

            final List<ClassificationProcess> repeated_processes = process.repeat(repetitions);
            final List<ClassificationMetrics> metrics = getClassificationMetrics(repeated_processes);
            final DataSet result_set = ClassificationMetrics.toDataSet(metrics);
            result_sets.add(result_set);
        }

        printSummarisedResults(row_labels, result_sets);

        return null; //void callable
    }

    protected abstract List<ClassificationProcess> getClassificationProcesses();

    protected List<ClassificationProcess> initClassificationProcesses(Classifier... classifiers) {

        final List<ClassificationProcess> processes = new ArrayList<>();
        for (Classifier classifier : classifiers) {
            processes.add(initClassificationProcess(classifier));
        }

        return processes;
    }

    private ClassificationProcess initClassificationProcess(Classifier classifier) {

        final Context context = new Context(new Random(SEED));
        context.setClassifier(classifier);

        final ClassificationProcess process = new ClassificationProcess(context);
        process.addStep(new LoadGoldStandardFromFile(gold_standard));
        process.addStep(new CleanGoldStandardRecords(ConsistentCodingCleaner.CORRECT));
        process.addStep(new SetTrainingRecordsByRatio(training_ratio));
        process.addStep(new TrainClassifier());
        process.addStep(new EvaluateClassifier(verbosity));

        return process;
    }

    private void exitWithErrorMessage(final String error_message) {

        System.err.println(error_message);
        commander.usage();
        System.exit(1);
    }

    private String getProcessName(final ClassificationProcess process) {

        return process.getContext().getClassifier().getName();
    }

    private List<ClassificationMetrics> getClassificationMetrics(final List<ClassificationProcess> repeated_processes) {

        final List<ClassificationMetrics> metrics = new ArrayList<>();
        for (ClassificationProcess repeated_process : repeated_processes) {
            metrics.add(repeated_process.getContext().getClassificationMetrics());
        }
        return metrics;
    }

    private void printSummarisedResults(final List<String> row_labels, final List<DataSet> result_sets) throws IOException {

        String table_caption = "\naggregate classifier performance (" + repetitions + " repetition" + (repetitions > 1 ? "s" : "") + "):\n";
        String first_column_heading = "classifier";

        TableGenerator table_generator = new TableGenerator(row_labels, result_sets, System.out, table_caption, first_column_heading, COLUMNS_AS_PERCENTAGES, '\t');

        if (verbosity != InfoLevel.NONE) {

            printDateStamp();
            table_generator.printTable();
        }
    }

    private void printDateStamp() {

        System.out.println("experiment run at: " + new Date());
    }
}
