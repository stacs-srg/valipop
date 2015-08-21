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
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.util.dataset.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * T
 * 
 * @author Masih Hajiarab Derkani
 */
public class MultipleClassifierExperiment implements Runnable {

    private static final TextCleaner DEFAULT_CLEANER = new EnglishStopWordCleaner().andThen(new PunctuationCleaner()).andThen(new LowerCaseCleaner());
    private static final long DEFAULT_RANDOM_SEED = 1413;
    private static final int ID_COLUMN_INDEX = 0;
    private static final int DATA_COLUMN_INDEX = 1;

    private final MultipleClassifier multiple_classifier;
    private final Random random;
    private final DataSet result;
    private final DataSet training;
    private final DataSet gold_standard;
    private final JCommander commander;
    private final Classifier core_classifier;

    @Parameter(names = "-t", description = "The Dataset to be used for traning the core classifier", required = true, converter = FileConverter.class)
    private File training_file;

    @Parameter(names = "-e", description = "The Dataset containing the ground truth about multiple classifications", required = true, converter = FileConverter.class)
    private File gold_standard_file;

    @Parameter(names = "-c", description = "The core classifier to use as part of multiple classification", required = true)
    private ClassifierSupplier core_classifier_supplier;

    @Parameter(names = "-t", description = "The classification confidence threshold", required = true)
    private double classification_confidence_threshold;

    @Parameter(names = "-p", description = "The cleaner to use for cleaning data prior to classification")
    private TextCleaner text_cleaner = DEFAULT_CLEANER;

    @Parameter(names = "-s", description = "Random seed")
    private long random_seed = DEFAULT_RANDOM_SEED;

    @Parameter(names = "-d", description = "The path to the file to store the classified data.", required = true, converter = FileConverter.class)
    private File destination;

    private MultipleClassifierExperiment(String... args) throws IOException {

        commander = new JCommander(this);
        commander.parse(args);

        random = new Random(random_seed);
        training = new DataSet(training_file.toPath());
        gold_standard = new DataSet(gold_standard_file.toPath());
        result = new DataSet(gold_standard.getColumnLabels());
        core_classifier = core_classifier_supplier.get();
        multiple_classifier = new MultipleClassifier(core_classifier, classification_confidence_threshold, text_cleaner);
    }

    public static void main(String... args) throws IOException {

        final MultipleClassifierExperiment experiment = new MultipleClassifierExperiment(args);
        experiment.run();
    }

    @Override
    public void run() {

        trainCoreClassifier();
        classify();
        persistClassificationResult();

        // generate confusion matrix using gold_standard and result datasets
        // print confusion matrix analysis
    }

    private void persistClassificationResult() {

        try (final BufferedWriter out = Files.newBufferedWriter(destination.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            result.print(out);
        }
        catch (IOException e) {
            throw new RuntimeException("unable to persist classification result", e);
        }
    }

    private void classify() {

        for (List<String> cells : gold_standard.getRecords()) {

            final String id = cells.get(ID_COLUMN_INDEX);
            final String data = cells.get(DATA_COLUMN_INDEX);
            final List<Classification> classifications = multiple_classifier.classify(data);
            final List<String> result_row = toDataSetRow(id, data, classifications);
            result.addRow(result_row);
        }
    }

    private List<String> toDataSetRow(final String id, final String data, final List<Classification> classifications) {

        final List<String> classification_codes = classifications.stream().map(Classification::getCode).collect(Collectors.toList());

        List<String> result_row = new ArrayList<>();
        result_row.add(id);
        result_row.add(data);
        result_row.addAll(classification_codes);
        return result_row;
    }

    private void trainCoreClassifier() {core_classifier.trainAndEvaluate(new Bucket(training), 1.0, random);}
}
