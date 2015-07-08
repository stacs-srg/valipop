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
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.ConsistentCodingCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.EnglishStopWordCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.PorterStemCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationProcess;
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
    private static final char TAB = '\t';
    private static final String FIRST_COLUMN_HEADING = "classifier";

    private static final String DEFAULT_GOLD_STANDARD_PATH = "src/test/resources/uk/ac/standrews/cs/digitising_scotland/record_classification/process/experiments/AbstractClassificationProcessTest/coded_data_1K.csv";
    private static final double DEFAULT_TRAINING_RATIO = 0.8;
    private static final int DEFAULT_REPETITIONS = 2;
    private static final InfoLevel DEFAULT_VERBOSITY = InfoLevel.LONG_SUMMARY;

    private static final List<Boolean> COLUMNS_AS_PERCENTAGES = Arrays.asList(true, true, true, true, true, true, false, false);
    private static final double ONE_MINUTE_IN_SECONDS = 60.0;

    private static final String DESCRIPTION_GOLD_STANDARD = "Path to a file containing the three column gold standard.";
    private static final String DESCRIPTION_REPETITION = "The number of repetitions.";
    private static final String DESCRIPTION_VERBOSITY = "The level of output verbosity.";
    private static final String DESCRIPTION_RATIO = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).";
    private static final String DESCRIPTION_DELIMITER = "The delimiter character of three column gold standard data.";

    private static final String INCONSISTENT_PARAM_NUMBERS_ERROR_MESSAGE = "the number of gold standard files must be equal to the number of training ratios";
    private static final String TABLE_CAPTION_STRING = "\naggregate classifier performance (%d repetition%s):\n";
    private static final String TRAINING_TIME_HEADER = "training time (m)";
    private static final String EVALUATION_TIME_HEADER = "evaluation time (m)";

    private final JCommander commander;

    @Parameter(names = {"-v", "--verbosity"}, description = DESCRIPTION_VERBOSITY)
    private InfoLevel verbosity = DEFAULT_VERBOSITY;

    @Parameter(names = {"-r", "--repetitionCount"}, description = DESCRIPTION_REPETITION)
    private int repetitions = DEFAULT_REPETITIONS;

    @Parameter(names = {"-g", "--goldStandard"}, description = DESCRIPTION_GOLD_STANDARD, listConverter = FileConverter.class)
    private List<File> gold_standard_files = Arrays.asList(new File(DEFAULT_GOLD_STANDARD_PATH));

    @Parameter(names = {"-t", "--trainingRecordRatio"}, description = DESCRIPTION_RATIO)
    private List<Double> training_ratios = Arrays.asList(DEFAULT_TRAINING_RATIO);

    @Parameter(names = {"-d", "--delimiter"}, description = DESCRIPTION_DELIMITER)
    private char delimiter = '|';

    public static final Cleaner[] CLEANERS = new Cleaner[]{new EnglishStopWordCleaner(), new PorterStemCleaner(), ConsistentCodingCleaner.CORRECT};

    protected Experiment() {

        commander = new JCommander(this);
    }

    protected Experiment(final String[] args) {

        this();

        try {
            parse(args);

        } catch (ParameterException e) {
            exitWithErrorMessage(e.getMessage());
        }
    }

    @Override
    public Void call() throws Exception {

        if (verbosity != InfoLevel.NONE) {
            printSummarisedResults(getExperimentResults());
        }

        return null; //void callable
    }

    public void setVerbosity(final InfoLevel verbosity) {

        this.verbosity = verbosity;
    }

    public void setRepetitions(final int repetitions) {

        this.repetitions = repetitions;
    }

    public void setGoldStandardFiles(final List<File> gold_standard_files) {

        this.gold_standard_files = gold_standard_files;
    }

    public void setTrainingRatios(final List<Double> training_ratios) {

        this.training_ratios = training_ratios;
    }

    public List<RepetitionResult> getExperimentResults() throws Exception {

        final List<ClassificationProcess> processes = getClassificationProcesses();
        final Map<ClassificationProcess, RepetitionResult> results = makeResultsMap(processes);

        // Loop nesting is this way round to avoid performing all repetitions of a given process consecutively, given
        // that timing information is being recorded.
        for (int i = 0; i < repetitions; i++) {

            for (final ClassificationProcess process : processes) {
                callProcess(process, results);
            }
        }
        return new ArrayList<>(results.values());
    }

    /**
     * Initialises a fresh list of classification process for each repetition.
     *
     * @return the list of classification processes to be run for a single repetition.
     * @throws IOException              if an error occurs while reading the input data
     * @throws InputFileFormatException if the input data files are in an unsupported format
     */
    protected abstract List<ClassificationProcess> getClassificationProcesses() throws IOException, InputFileFormatException;

    protected List<ClassificationProcess> getClassificationProcesses(final Classifier... classifiers) throws IOException, InputFileFormatException {

        final List<ClassificationProcess> processes = new ArrayList<>();

        for (final Classifier classifier : classifiers) {
            processes.add(getClassificationProcess(classifier));
        }

        return processes;
    }

    private void callProcess(final ClassificationProcess process, final Map<ClassificationProcess, RepetitionResult> results) throws Exception {

        final RepetitionResult result = results.get(process);
        final ClassificationContext context = new ClassificationContext(process);

        process.call(context);

        result.data_set.addRow(getResultValues(context));
        result.contexts.add(context);
    }

    private List<String> getResultValues(final ClassificationContext context) {

        final List<String> values = context.getClassificationMetrics().getValues();

        values.add(String.valueOf(context.getTrainingTime().getSeconds() / ONE_MINUTE_IN_SECONDS));
        values.add(String.valueOf(context.getClassificationTime().getSeconds() / ONE_MINUTE_IN_SECONDS));

        return values;
    }

    private Map<ClassificationProcess, RepetitionResult> makeResultsMap(final List<ClassificationProcess> processes) {

        final Map<ClassificationProcess, RepetitionResult> results = new HashMap<>();

        for (final ClassificationProcess process : processes) {
            results.put(process, makeRepetitionResult(process));
        }

        return results;
    }

    private RepetitionResult makeRepetitionResult(final ClassificationProcess process) {

        return new RepetitionResult(process.getClassifier().getName(), new DataSet(getDataSetLabelsWithTimingColumns()));
    }

    private List<String> getDataSetLabelsWithTimingColumns() {

        final List<String> labels = new ArrayList<>(ClassificationMetrics.DATASET_LABELS);
        labels.add(TRAINING_TIME_HEADER);
        labels.add(EVALUATION_TIME_HEADER);
        return labels;
    }

    private ClassificationProcess getClassificationProcess(final Classifier classifier) throws IOException, InputFileFormatException {

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
            throw new ParameterException(INCONSISTENT_PARAM_NUMBERS_ERROR_MESSAGE);
        }
    }

    private Step makeAddRecordsStep(final int gold_standard_file_number) throws IOException, InputFileFormatException {

        final File gold_standard_file = gold_standard_files.get(gold_standard_file_number);
        final Double training_ratio = training_ratios.get(gold_standard_file_number);

        return new AddTrainingAndEvaluationRecordsByRatio(new Bucket(gold_standard_file), training_ratio, CLEANERS);
    }

    private void exitWithErrorMessage(final String error_message) {

        System.err.println(error_message);
        commander.usage();
        System.exit(1);
    }

    private void printSummarisedResults(final List<RepetitionResult> results) throws IOException {

        final String table_caption = String.format(TABLE_CAPTION_STRING, repetitions, getPluralitySuffix(repetitions));
        final TableGenerator table_generator = new TableGenerator(getNames(results), getDataSets(results), System.out, table_caption, FIRST_COLUMN_HEADING, COLUMNS_AS_PERCENTAGES, TAB);

        printDateStamp();
        table_generator.printTable();
    }

    private static String getPluralitySuffix(final int repetitions) {

        return repetitions > 1 ? "s" : "";
    }

    private List<String> getNames(final List<RepetitionResult> results) {

        return results.stream().map(result -> result.name).collect(Collectors.toList());
    }

    private List<DataSet> getDataSets(final List<RepetitionResult> results) {

        return results.stream().map(result -> result.data_set).collect(Collectors.toList());
    }

    private void printDateStamp() {

        System.out.println("experiment run at: " + new Date());
    }

    class RepetitionResult {

        final String name;
        final DataSet data_set;
        final List<ClassificationContext> contexts;

        public RepetitionResult(String name, DataSet data_set) {

            this.name = name;
            this.data_set = data_set;
            contexts = new ArrayList<>();
        }
    }
}
