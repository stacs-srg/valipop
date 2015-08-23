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
package uk.ac.standrews.cs.digitising_scotland.record_classification.multiple_classifier;

import com.beust.jcommander.*;
import com.beust.jcommander.converters.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.util.dataset.*;
import uk.ac.standrews.cs.util.tools.*;
import uk.ac.standrews.cs.util.tools.InfoLevel;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class MultipleClassifierExperiment implements Runnable {

    private static final long DEFAULT_RANDOM_SEED = 1413;
    private static final Charset DEFAULT_DESTINATION_CHARSET = StandardCharsets.UTF_8;
    private static final int ID_COLUMN_INDEX = 0;
    private static final int DATA_COLUMN_INDEX = 1;

    private final MultipleClassifier multiple_classifier;
    private final Random random;
    private final DataSet classified_records;
    private final DataSet training;
    private final DataSet gold_standard;
    private final JCommander commander;
    private final Classifier core_classifier;
    private final TextCleaner text_cleaner;

    @Parameter(names = "-t", description = "The data set to be used for training the core classifier.", required = true, converter = FileConverter.class)
    private File training_file;

    @Parameter(names = "-g", description = "The data set containing the ground truth about multiple classifications.", required = true, converter = FileConverter.class)
    private File gold_standard_file;

    @Parameter(names = "-c", description = "The core classifier to use as part of multiple classification.", required = true)
    private ClassifierSupplier core_classifier_supplier;

    @Parameter(names = "-tr", description = "The classification confidence threshold.", required = true)
    private double classification_confidence_threshold;

    @Parameter(names = "-p", description = "The cleaner to use for cleaning data prior to classification.")
    private TextCleanerSupplier text_cleaner_supplier = TextCleanerSupplier.ALL;

    @Parameter(names = "-s", description = "Random seed")
    private long random_seed = DEFAULT_RANDOM_SEED;

    @Parameter(names = "-d", description = "The path to the file in which to store the classified data.", required = true, converter = FileConverter.class)
    private File destination;

    @Parameter(names = "-v", description = "Logging verbosity.")
    private InfoLevel verbosity = InfoLevel.VERBOSE;

    private MultipleClassifierExperiment(String... args) throws IOException {

        commander = new JCommander(this);
        commander.parse(args);

        random = new Random(random_seed);
        training = new DataSet(training_file.toPath());
        gold_standard = new DataSet(gold_standard_file.toPath());
        classified_records = new DataSet(gold_standard.getColumnLabels());
        core_classifier = core_classifier_supplier.get();
        text_cleaner = text_cleaner_supplier.get();
        multiple_classifier = new MultipleClassifier(core_classifier, classification_confidence_threshold, text_cleaner, (one, another) -> true);
        Logging.setInfoLevel(verbosity);
    }

    public static void main(String... args) throws IOException {

        final MultipleClassifierExperiment experiment = new MultipleClassifierExperiment(args);
        experiment.run();
    }

    @Override
    public void run() {

        logParameters();
        trainCoreClassifier();
        classify();
        persistClassificationResults();
        logClassificationMetrics();
    }

    private void logClassificationMetrics() {

        final StrictConfusionMatrix confusion_matrix = new StrictConfusionMatrix(classified_records, gold_standard, new ConsistentCodingChecker());
        final ClassificationMetrics classification_metrics = new ClassificationMetrics(confusion_matrix);
        classification_metrics.printMetrics();
    }

    private void logParameters() {

        Logging.output(InfoLevel.VERBOSE, String.format("Core classifier: %s", core_classifier.getName()));
        Logging.output(InfoLevel.VERBOSE, String.format("Classification confidence threshold: %f", classification_confidence_threshold));
        Logging.output(InfoLevel.VERBOSE, String.format("Pre-classification data cleaner: %s", String.valueOf(text_cleaner_supplier)));
        Logging.output(InfoLevel.VERBOSE, String.format("Training dataset: %s", String.valueOf(training_file)));
        Logging.output(InfoLevel.VERBOSE, String.format("Gold standard dataset: %s", String.valueOf(gold_standard_file)));
    }

    private void persistClassificationResults() {

        Logging.output(InfoLevel.VERBOSE, String.format("Persisting classified records at %s", String.valueOf(destination)));
        try (final BufferedWriter out = Files.newBufferedWriter(destination.toPath(), DEFAULT_DESTINATION_CHARSET, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            classified_records.print(out);
            Logging.output(InfoLevel.VERBOSE, "Done persisting classified records.");
        }
        catch (IOException e) {
            throw new RuntimeException("unable to persist classification results", e);
        }
    }

    private void classify() {

        final int gold_standard_size = gold_standard.getRecords().size();
        Logging.setProgressIndicatorSteps(gold_standard_size);
        Logging.output(InfoLevel.VERBOSE, String.format("Classifying %d records...", gold_standard_size));

        final Instant start = Instant.now();
        for (List<String> cells : gold_standard.getRecords()) {

            final String id = cells.get(ID_COLUMN_INDEX);
            final String data = cells.get(DATA_COLUMN_INDEX);
            final List<Classification> classifications = multiple_classifier.classify(data);
            final List<String> result_row = toDataSetRow(id, data, classifications);
            classified_records.addRow(result_row);
            Logging.progressStep(InfoLevel.VERBOSE);
        }

        Logging.output(InfoLevel.VERBOSE, "Done classifying records in " + Formatting.format(Duration.between(start, Instant.now())));
    }

    private List<String> toDataSetRow(final String id, final String data, final List<Classification> classifications) {

        final List<String> row = new ArrayList<>();
        row.add(id);
        row.add(data);
        classifications.stream().map(Classification::getCode).forEach(row::add);
        return row;
    }

    private void trainCoreClassifier() {

        final Bucket consistent_cleaned_bucket = getTrainingBucket();
        Logging.output(InfoLevel.VERBOSE, String.format("Training core classifier with %d records...", consistent_cleaned_bucket.size()));
        final Instant start = Instant.now();
        core_classifier.trainAndEvaluate(consistent_cleaned_bucket, 1.0, random);
        Logging.output(InfoLevel.VERBOSE, "Done training core classifier in " + Formatting.format(Duration.between(start, Instant.now())));
    }

    private Bucket getTrainingBucket() {

        // TODO tidy up cleaning before training.
        Logging.output(InfoLevel.VERBOSE, String.format("Cleaning %d records prior to training core classifier...", training.getRecords().size()));
        final Instant start = Instant.now();

        final Bucket training_bucket = new Bucket(training);
        final Bucket cleaned_bucket = new Bucket();
        training_bucket.forEach(record -> cleaned_bucket.add(text_cleaner.cleanRecord(record)));
        final Bucket consistent_cleaned_training_bucket = ConsistentClassificationCleaner.CORRECT.apply(Collections.singletonList(cleaned_bucket)).get(0);

        Logging.output(InfoLevel.VERBOSE, "Done cleaning training records in " + Formatting.format(Duration.between(start, Instant.now())));

        return consistent_cleaned_training_bucket;
    }
}

enum TextCleanerSupplier implements Supplier<TextCleaner> {

    STOP_WORD(EnglishStopWordCleaner::new),
    PUNCTUATION(PunctuationCleaner::new),
    LOWER_CASE(LowerCaseCleaner::new),
    STEMMING(StemmingCleaner::new),
    ALL(() -> new EnglishStopWordCleaner().andThen(new PunctuationCleaner()).andThen(new LowerCaseCleaner()).andThen(new StemmingCleaner()));

    private final Supplier<TextCleaner> supplier;

    TextCleanerSupplier(final Supplier<TextCleaner> supplier) { this.supplier = supplier; }

    @Override
    public TextCleaner get() { return supplier.get(); }
}
