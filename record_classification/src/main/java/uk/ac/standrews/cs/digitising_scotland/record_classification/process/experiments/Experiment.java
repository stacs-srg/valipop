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
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
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
    private final Random random;

    @Parameter(names = {"-v", "--verbosity"}, description = "The level of output verbosity.")
    protected InfoLevel verbosity = InfoLevel.LONG_SUMMARY;

    @Parameter(names = {"-r", "--repetitionCount"}, description = "The number of repetitions.")
    protected int repetitions = 2;

    @Parameter(names = {"-g", "--goldStandard"}, description = "Path to a file containing the three column gold standard.", listConverter = FileConverter.class)
    protected List<File> gold_standard_files = Arrays.asList(new File("src/test/resources/uk/ac/standrews/cs/digitising_scotland/record_classification/process/experiments/AbstractClassificationTest/gold_standard_small.csv"));

    @Parameter(names = {"-t", "--trainingRecordRatio"}, description = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).")
    protected List<Double> training_ratios = Arrays.asList(0.8);

    @Parameter(names = {"-d", "--delimiter"}, description = "The delimiter character of three column gold standard data.")
    protected char delimiter = '|';
    public static final String FIRST_COLUMN_HEADING = "classifier";

    protected Experiment(String[] args) {

        commander = new JCommander(this);
        try {
            parse(args);
        }
        catch (ParameterException e) {
            exitWithErrorMessage(e.getMessage());
        }
        random = new Random(SEED);
    }

    private void parse(final String[] args) {

        commander.parse(args);

        if (gold_standard_files.size() != training_ratios.size()) {
            throw new ParameterException("the number of gold standard files must be equal to the number of training ratios");
        }
    }

    @Override
    public Void call() throws Exception {

        final Map<String, DataSet> results = new HashMap<>();

        for (int i = 0; i < repetitions; i++) {

            final List<ClassificationProcess> processes = initClassificationProcesses();

            for (ClassificationProcess process : processes) {

                process.call();

                final String name = getProcessName(process);
                final ClassificationMetrics metrics = process.getContext().getClassificationMetrics();
                final DataSet metrics_dataSet = metrics.toDataSet();

                if (results.containsKey(name)) {
                    final DataSet result_dataset = results.get(name);
                    metrics_dataSet.getRecords().forEach(result_dataset::addRow);
                }
                else {
                    results.put(name, metrics_dataSet);
                }
            }
        }

        printSummarisedResults(results);

        return null; //void callable
    }

    /**
     * Initialises a fresh list of classification process for each repetition.
     *
     * @return the list of classification processes to be run for a single repetition.
     * @throws IOException if an error occurs while reading the input data
     * @throws InputFileFormatException if the input data files are in an unsupported format
     */
    protected abstract List<ClassificationProcess> initClassificationProcesses() throws IOException, InputFileFormatException;

    protected List<ClassificationProcess> initClassificationProcessesFromClassifiers(Classifier... classifiers) throws IOException, InputFileFormatException {

        final List<ClassificationProcess> processes = new ArrayList<>();
        for (Classifier classifier : classifiers) {
            processes.add(initClassificationProcessFromClassifier(classifier));
        }

        return processes;
    }

    private ClassificationProcess initClassificationProcessFromClassifier(Classifier classifier) throws IOException, InputFileFormatException {

        final Context context = new Context(random);
        context.setClassifier(classifier);

        final ClassificationProcess process = new ClassificationProcess(context);

        for (int i = 0; i < gold_standard_files.size(); i++) {

            final File gold_standard_file = gold_standard_files.get(i);
            final Double training_ratio = training_ratios.get(i);
            final Bucket gold_standard = new Bucket(gold_standard_file);
            process.addStep(new AddTrainingAndEvaluationRecordsByRatio(gold_standard, training_ratio, new EnglishStopWordCleaner(), new PorterStemCleaner(), ConsistentCodingCleaner.CORRECT));
        }

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

    private void printSummarisedResults(final Map<String, DataSet> results) throws IOException {

        final String table_caption = String.format("\naggregate classifier performance (%d repetition%s):\n", repetitions, repetitions > 1 ? "s" : "");
        final List<String> row_labels = new ArrayList<>(results.keySet());
        final List<DataSet> result_sets = new ArrayList<>(results.values());
        final TableGenerator table_generator = new TableGenerator(row_labels, result_sets, System.out, table_caption, FIRST_COLUMN_HEADING, COLUMNS_AS_PERCENTAGES, '\t');

        if (verbosity != InfoLevel.NONE) {

            printDateStamp();
            table_generator.printTable();
        }
    }

    private void printDateStamp() {

        System.out.println("experiment run at: " + new Date());
    }
}
