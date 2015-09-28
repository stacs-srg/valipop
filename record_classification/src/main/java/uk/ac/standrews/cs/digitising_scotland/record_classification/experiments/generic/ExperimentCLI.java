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

import com.beust.jcommander.*;
import com.beust.jcommander.converters.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.util.dataset.*;
import uk.ac.standrews.cs.util.tools.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

/**
 * @author masih
 */
public class ExperimentCLI extends Experiment {

    @Parameter(names = {"-c", "--classifierSupplier"}, description = "The classifier to use for experiment.", required = true)
    private ClassifierSupplier classifier_supplier;

    @Parameter(names = {"-u", "--unseenData"}, description = "Path to unseen data to be classified.", converter = PathConverter.class)
    private Path unseen_data_path;

    @Parameter(names = {"-o", "--classifiedRecordsOutput"}, description = "Path to which to persist the classified unseen data.", converter = PathConverter.class)
    private Path classified_unseen_data_path;

    protected ExperimentCLI(final String[] args) throws IOException, InputFileFormatException {

        super(args);
    }

    @Override
    protected List<Supplier<Classifier>> getClassifierFactories() throws IOException, InputFileFormatException {

        return Collections.singletonList(classifier_supplier);
    }

    public static void main(String[] args) throws Exception {

        final ExperimentCLI experiment = new ExperimentCLI(args);
        experiment.call();

    }

    @Override
    public Void call() throws Exception {

        Logging.setInfoLevel(verbosity);

        final List<ClassifierResults> results = runExperiment();

        if (unseen_data_path != null) {
            classifyUnseenRecords(results);
        }

        printSummarisedResults(results);

        return null; //void callable
    }

    private void classifyUnseenRecords(final List<ClassifierResults> results) throws IOException {

        //TODO embed this into the steps of the last classification process.
        final ClassifierResults last_results = getLast(results);
        final Classifier classifier = last_results.getContexts().get(0).getClassifier();
        final DataSet unseen_data_set = new DataSet(Files.newBufferedReader(unseen_data_path));
        final Bucket unseen_data_bucket = new Bucket(unseen_data_set);
        final Bucket classified_unseen_data = classifier.classify(unseen_data_bucket);
        final List<String> classified_dataset_labels = new ArrayList<>(unseen_data_set.getColumnLabels());
        classified_dataset_labels.add("CONFIDENCE");
        classified_dataset_labels.add("DETAILS");
        
        final DataSet classified_unseen_data_set = classified_unseen_data.toDataSet(classified_dataset_labels);

        if (classified_unseen_data_path != null) {
            try (final BufferedWriter out = Files.newBufferedWriter(classified_unseen_data_path)) {
                classified_unseen_data_set.print(out);
            }
        }
        else {
            System.out.println();
            System.out.println("no destination is specified to persist classified unseen data; printing data into stdout instead.");
            System.out.println();
            System.out.println();
            classified_unseen_data_set.print(System.out);
            System.out.println();
            System.out.println();
        }
    }

    private ClassifierResults getLast(final List<ClassifierResults> results) {

        return results != null && !results.isEmpty() ? results.get(results.size() - 1) : null;
    }
}
