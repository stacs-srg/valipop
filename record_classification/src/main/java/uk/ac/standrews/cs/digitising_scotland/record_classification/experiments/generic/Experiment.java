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
package uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.generic;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.ConsistentClassificationCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.EnglishStopWordCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.StemmingCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassifierFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.specific.EvaluationExperimentProcess;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tables.TableGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public abstract class Experiment implements Callable<Void> {

    private static final long SEED = 32498723497239578L;
    private static final char TAB = '\t';
    private static final String FIRST_COLUMN_HEADING = "classifier";

    private static final String DEFAULT_GOLD_STANDARD_PATH = "src/test/resources/uk/ac/standrews/cs/digitising_scotland/record_classification/experiments/AbstractClassificationProcessTest/coded_data_1K.csv";
    private static final double DEFAULT_TRAINING_RATIO = 0.8;
    private static final int DEFAULT_REPETITIONS = 2;
    private static final InfoLevel DEFAULT_VERBOSITY = InfoLevel.LONG_SUMMARY;

    private static final List<Boolean> COLUMNS_AS_PERCENTAGES = Arrays.asList(true, true, true, true, false, false);
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
    private Collection<ClassificationProcess> processes;

    @Parameter(names = {"-v", "--verbosity"}, description = DESCRIPTION_VERBOSITY)
    private InfoLevel verbosity = DEFAULT_VERBOSITY;

    @Parameter(names = {"-r", "--repetitionCount"}, description = DESCRIPTION_REPETITION)
    private int repetitions = DEFAULT_REPETITIONS;

    @Parameter(names = {"-g", "--goldStandard"}, description = DESCRIPTION_GOLD_STANDARD, listConverter = PathConverter.class)
    private List<Path> gold_standard_files = Arrays.asList(Paths.get(DEFAULT_GOLD_STANDARD_PATH));

    @Parameter(names = {"-t", "--trainingRecordRatio"}, description = DESCRIPTION_RATIO)
    private List<Double> training_ratios = Arrays.asList(DEFAULT_TRAINING_RATIO);

    @Parameter(names = {"-d", "--delimiter"}, description = DESCRIPTION_DELIMITER)
    private char delimiter = '|';

    public static final List<Cleaner> CLEANERS = Arrays.asList(new EnglishStopWordCleaner(), new StemmingCleaner(), ConsistentClassificationCleaner.CORRECT);

    protected Experiment() throws IOException, InputFileFormatException {

        commander = new JCommander(this);
    }

    protected Experiment(final String[] args) throws IOException, InputFileFormatException {

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

    public void setGoldStandardFiles(final List<Path> gold_standard_files) {

        this.gold_standard_files = gold_standard_files;
    }

    public void setTrainingRatios(final List<Double> training_ratios) {

        this.training_ratios = training_ratios;
    }

    public List<ClassifierResults> getExperimentResults() throws Exception {

        final List<ClassifierFactory> classifier_factories = getClassifierFactories();

        final Map<ClassifierFactory, ClassificationProcess> process_map = makeProcessMap(classifier_factories);
        final Map<ClassifierFactory, ClassifierResults> result_map = makeResultsMap(classifier_factories);
        final Map<ClassifierFactory, Random> random_map = makeRandomMap(classifier_factories);

        // Loop nesting is this way round to avoid performing all repetitions of a given process consecutively, given
        // that timing information is being recorded.
        for (int i = 0; i < repetitions; i++) {

            for (ClassifierFactory factory : classifier_factories) {

                final ClassificationProcess process = process_map.get(factory);
                final ClassifierResults result = result_map.get(factory);
                final Random random = random_map.get(factory);

                final ClassificationContext context = new ClassificationContext(factory.get(), random);
                context.setVerbosity(verbosity);

                process.call(context);

                result.data_set.addRow(getResultValuesWithTimings(context));
                result.contexts.add(context);
            }
        }

        processes = process_map.values();

        return new ArrayList<>(result_map.values());
    }

    public Collection<ClassificationProcess> getProcesses() {

        return processes;
    }

    protected abstract List<ClassifierFactory> getClassifierFactories() throws IOException, InputFileFormatException;

    private ClassificationProcess makeClassificationProcess(final ClassifierFactory factory) {

        final EvaluationExperimentProcess process = new EvaluationExperimentProcess();

        process.setGoldStandardFiles(gold_standard_files);
        process.setTrainingRatios(training_ratios);
        process.setCleaners(CLEANERS);

        process.configureSteps();

        return process;
    }

    private List<String> getResultValuesWithTimings(final ClassificationContext context) {

        final ClassificationMetrics classificationMetrics = context.getClassificationMetrics();
        final List<String> values = classificationMetrics.getValues();

        values.add(String.valueOf(context.getTrainingTime().getSeconds() / ONE_MINUTE_IN_SECONDS));
        values.add(String.valueOf(context.getClassificationTime().getSeconds() / ONE_MINUTE_IN_SECONDS));

        return values;
    }

    private Map<ClassifierFactory, Random> makeRandomMap(final List<ClassifierFactory> factories) {

        return factories.stream().collect(Collectors.toMap(Function.identity(), this::makeRandom));
    }

    private Map<ClassifierFactory, ClassifierResults> makeResultsMap(final List<ClassifierFactory> factories) {

        return factories.stream().collect(Collectors.toMap(Function.identity(), this::makeClassifierResults));
    }

    private Map<ClassifierFactory, ClassificationProcess> makeProcessMap(final List<ClassifierFactory> factories) {

        return factories.stream().collect(Collectors.toMap(Function.identity(), this::makeClassificationProcess));
    }

    private Random makeRandom(final ClassifierFactory factory) {

        return new Random(SEED);
    }

    private ClassifierResults makeClassifierResults(final ClassifierFactory factory) {

        return new ClassifierResults(factory.get().getName(), new DataSet(getDataSetLabelsWithTimingColumns()));
    }

    private List<String> getDataSetLabelsWithTimingColumns() {

        final List<String> labels = new ArrayList<>(ClassificationMetrics.DATASET_LABELS);
        labels.add(TRAINING_TIME_HEADER);
        labels.add(EVALUATION_TIME_HEADER);
        return labels;
    }

    private void parse(final String[] args) {

        commander.parse(args);

        if (gold_standard_files.size() != training_ratios.size()) {
            throw new ParameterException(INCONSISTENT_PARAM_NUMBERS_ERROR_MESSAGE);
        }
    }

    private void exitWithErrorMessage(final String error_message) {

        System.err.println(error_message);
        commander.usage();
        System.exit(1);
    }

    private void printSummarisedResults(final List<ClassifierResults> results) throws IOException {

        final String table_caption = String.format(TABLE_CAPTION_STRING, repetitions, getPluralitySuffix(repetitions));
        final TableGenerator table_generator = new TableGenerator(getNames(results), getDataSets(results), System.out, table_caption, FIRST_COLUMN_HEADING, COLUMNS_AS_PERCENTAGES, TAB);

        printDateStamp();
        table_generator.printTable();
    }

    private static String getPluralitySuffix(final int repetitions) {

        return repetitions > 1 ? "s" : "";
    }

    private List<String> getNames(final List<ClassifierResults> results) {

        return results.stream().map(result -> result.name).collect(Collectors.toList());
    }

    private List<DataSet> getDataSets(final List<ClassifierResults> results) {

        return results.stream().map(result -> result.data_set).collect(Collectors.toList());
    }

    private void printDateStamp() {

        System.out.println("experiment run at: " + new Date());
    }

    public class ClassifierResults {

        final String name;
        final DataSet data_set;
        final List<ClassificationContext> contexts;

        public ClassifierResults(String name, DataSet data_set) {

            this.name = name;
            this.data_set = data_set;
            contexts = new ArrayList<>();
        }

        public List<ClassificationContext> getContexts() {

            return contexts;
        }
    }
}
