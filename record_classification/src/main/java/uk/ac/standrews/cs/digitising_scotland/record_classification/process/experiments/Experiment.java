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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.ConsistentCodingCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.EnglishStopWordCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.PorterStemCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.Step;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.AddTrainingAndEvaluationRecordsByRatio;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.EvaluateClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.TrainClassifier;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tables.TableGenerator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public abstract class Experiment implements Callable<Void> {

    private static final long SEED = 32498723497239578L;
    private static final List<Boolean> COLUMNS_AS_PERCENTAGES = Arrays.asList(true, true, true, true, true, true, true, true, false, false);
    private static final String FIRST_COLUMN_HEADING = "classifier";
    private static final char TAB = '\t';

    private static final String DEFAULT_GOLD_STANDARD_PATH = "src/test/resources/uk/ac/standrews/cs/digitising_scotland/record_classification/process/experiments/AbstractClassificationProcessTest/coded_data_1K.csv";

    private static final String DESCRIPTION_GOLD_STANDARD = "Path to a file containing the three column gold standard.";
    private static final String DESCRIPTION_REPETITION = "The number of repetitions.";
    private static final String DESCRIPTION_VERBOSITY = "The level of output verbosity.";
    private static final String DESCRIPTION_RATIO = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).";
    private static final String DESCRIPTION_DELIMITER = "The delimiter character of three column gold standard data.";

    private final JCommander commander;

    @Parameter(names = {"-v", "--verbosity"}, description = DESCRIPTION_VERBOSITY)
    private InfoLevel verbosity = InfoLevel.LONG_SUMMARY;

    @Parameter(names = {"-r", "--repetitionCount"}, description = DESCRIPTION_REPETITION)
    private int repetitions = 2;

    @Parameter(names = {"-g", "--goldStandard"}, description = DESCRIPTION_GOLD_STANDARD, listConverter = FileConverter.class)
    private List<File> gold_standard_files = Arrays.asList(new File(DEFAULT_GOLD_STANDARD_PATH));

    @Parameter(names = {"-t", "--trainingRecordRatio"}, description = DESCRIPTION_RATIO)
    private List<Double> training_ratios = Arrays.asList(0.8);

    @Parameter(names = {"-d", "--delimiter"}, description = DESCRIPTION_DELIMITER)
    private char delimiter = '|';

    protected Experiment() {

        commander = new JCommander(this);
    }

    protected Experiment(String[] args) {

        this();

        try {
            parse(args);

        } catch (ParameterException e) {
            exitWithErrorMessage(e.getMessage());
        }
    }

    @Override
    public Void call() throws Exception {

        final List<ExperimentResult> results = getExperimentResults();

        printSummarisedResults(results);

        return null; //void callable
    }

    public void setVerbosity(InfoLevel verbosity) {

        this.verbosity = verbosity;
    }

    public void setRepetitions(int repetitions) {

        this.repetitions = repetitions;
    }

    public void setGoldStandardFiles(List<File> gold_standard_files) {

        this.gold_standard_files = gold_standard_files;
    }

    public void setTrainingRatios(List<Double> training_ratios) {

        this.training_ratios = training_ratios;
    }

    public List<ExperimentResult> getExperimentResults() throws Exception {

        final List<ClassificationProcess> processes = getClassificationProcesses();
        final List<ExperimentResult> results = new ArrayList<>();

        for (ClassificationProcess process : processes) {

            results.add(new ExperimentResult(getProcessName(process), new DataSet(ClassificationMetrics.DATASET_LABELS)));
        }

        for (int i = 0; i < repetitions; i++) {

            for (ClassificationProcess process : processes) {

                final Classifier classifier = process.getClassifier();
                final ClassificationContext context = new ClassificationContext(classifier, process.getRandom());

                process.call(context);

                final ClassificationMetrics metrics = context.getClassificationMetrics();

                final ExperimentResult result = getExperimentResult(classifier.getName(), results);

                result.data_set.addRow(metrics.getValues());
                result.repetition_contexts.add(context);
            }
        }
        return results;
    }

    private ExperimentResult getExperimentResult(String name, List<ExperimentResult> results) {

        for (ExperimentResult result : results) {
            if (result.name.equals(name)) {
                return result;
            }
        }
        return null;
    }

    class ExperimentResult {

        public String name;
        public DataSet data_set;
        public List<ClassificationContext> repetition_contexts;

        public ExperimentResult(String name, DataSet data_set) {

            this.name = name;
            this.data_set = data_set;
            repetition_contexts = new ArrayList<>();
        }
    }
//
//    protected void addResults(Map<String, DataSet> results, ClassificationProcess process) {
//
//        final String name = getProcessName(process);
//        final ClassificationMetrics metrics = process.getContext().getClassificationMetrics();
//        final DataSet metrics_data_set = metrics.toDataSet();
//
//        if (results.containsKey(name)) {
//            final DataSet result_data_set = results.get(name);
//            metrics_data_set.getRecords().forEach(result_data_set::addRow);
//        } else {
//            results.put(name, metrics_data_set);
//        }
//    }

    /**
     * Initialises a fresh list of classification process for each repetition.
     *
     * @return the list of classification processes to be run for a single repetition.
     * @throws IOException              if an error occurs while reading the input data
     * @throws InputFileFormatException if the input data files are in an unsupported format
     */
    protected abstract List<ClassificationProcess> getClassificationProcesses() throws IOException, InputFileFormatException;

    protected List<ClassificationProcess> getClassificationProcesses(Classifier... classifiers) throws IOException, InputFileFormatException {

        final List<ClassificationProcess> processes = new ArrayList<>();
        for (Classifier classifier : classifiers) {
            processes.add(getClassificationProcess(classifier));
        }

        return processes;
    }

    private ClassificationProcess getClassificationProcess(Classifier classifier) throws IOException, InputFileFormatException {

        final ClassificationProcess process = new ClassificationProcess(classifier, new Random(SEED));

        for (int i = 0; i < gold_standard_files.size(); i++) {
            process.addStep(makeAddRecordsStep(i));
        }

        process.addStep(new TrainClassifier());
        process.addStep(new EvaluateClassifier(verbosity));

        return process;
    }

    private void parse(final String[] args) {

        commander.parse(args);

        if (gold_standard_files.size() != training_ratios.size()) {
            throw new ParameterException("the number of gold standard files must be equal to the number of training ratios");
        }
    }

    private Step makeAddRecordsStep(int gold_standard_file_number) throws IOException, InputFileFormatException {

        final File gold_standard_file = gold_standard_files.get(gold_standard_file_number);
        final Bucket gold_standard = new Bucket(gold_standard_file);

        final Double training_ratio = training_ratios.get(gold_standard_file_number);

        return new AddTrainingAndEvaluationRecordsByRatio(gold_standard, training_ratio, new EnglishStopWordCleaner(), new PorterStemCleaner(), ConsistentCodingCleaner.CORRECT);
    }

    private void exitWithErrorMessage(final String error_message) {

        System.err.println(error_message);
        commander.usage();
        System.exit(1);
    }

    private String getProcessName(final ClassificationProcess process) {

        return process.getClassifier().getName();
    }

    private void printSummarisedResults(final List<ExperimentResult> results) throws IOException {

        final String table_caption = String.format("\naggregate classifier performance (%d repetition%s):\n", repetitions, repetitions > 1 ? "s" : "");
        final List<String> row_labels = getNames(results);
        final List<DataSet> result_sets = getDataSets(results);
        final TableGenerator table_generator = new TableGenerator(row_labels, result_sets, System.out, table_caption, FIRST_COLUMN_HEADING, COLUMNS_AS_PERCENTAGES, TAB);

        if (verbosity != InfoLevel.NONE) {

            printDateStamp();
            table_generator.printTable();
        }
    }

    private List<String> getNames(List<ExperimentResult> results) {

        return results.stream().map(result -> result.name).collect(Collectors.toList());
    }

    private List<DataSet> getDataSets(List<ExperimentResult> results) {

        return results.stream().map(result -> result.data_set).collect(Collectors.toList());
    }

    private void printDateStamp() {

        System.out.println("experiment run at: " + new Date());
    }
}
