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
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.*;
import uk.ac.standrews.cs.util.dataset.*;
import uk.ac.standrews.cs.util.tools.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author masih
 */
public class ExperimentCLI extends Experiment {

    public static final int CODE_INDEX = 2;
    public static final int LABEL_INDEX = 1;
    @Parameter(names = {"-c", "--classifierSupplier"}, description = "The classifier to use for experiment.", required = true)
    private ClassifierSupplier classifier_supplier;

    @Parameter(names = {"-u", "--unseenData"}, description = "Path to unseen data to be classified.", converter = PathConverter.class)
    private Path unseen_data_path;

    @Parameter(names = {"-o", "--classifiedRecordsOutput"}, description = "Path to which to persist the classified unseen data.", converter = PathConverter.class)
    private Path classified_unseen_data_path;

    @Parameter(names = {"-oe", "--classifiedEvaluationRecordsOutput"}, description = "Path to which to persist the classified evaluation data.", converter = PathConverter.class)
    private Path classified_evaluation_data_path;

    @Parameter(names = {"-h", "--codingLookupCSV"}, description = "Path to coding scheme description lookup; a 3 column csv: id, label and code", converter = PathConverter.class)
    private Path code_label_lookup_path;

    private final Map<String, String> code_label_lookup = new HashMap<>();

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

        populateCodeLabelLookup();

        if (classified_evaluation_data_path != null) {
            persistClassifiedEvaluationRecords(results);
        }

        if (unseen_data_path != null) {
            classifyUnseenRecords(results);
        }

        printSummarisedResults(results);

        return null; //void callable
    }

    private void persistClassifiedEvaluationRecords(final List<ClassifierResults> results) throws IOException {

        final ClassificationContext context = getLast(results).getContexts().get(0);
        final Bucket evaluation_records = context.getEvaluationRecords();
        final Bucket evaluation_classified_records = context.getConfusionMatrix().getClassifiedRecords();
        final Stream<Record> evaluation_records_stream = StreamSupport.stream(evaluation_records.spliterator(), false);

        final DataSet evaluation_output = new DataSet(Arrays.asList("ID", "RAW_DATA", "GOLD_STANDARD_CODE", "GOLD_STANDARD_SCHEME_LABEL", "OUTPUT_CODE", "OUTPUT_CODE_SCHEME_LABEL", "ANCESTOR_DISTANCE", "CONFIDENCE"));
        for (Record record : evaluation_classified_records) {

            final int id = record.getId();
            final Classification classification = record.getClassification();
            final Record gold_standard_record = evaluation_records_stream.filter(rec -> id == rec.getId()).findFirst().get();
            final Classification gold_standard_classification = gold_standard_record.getClassification();

            final String id_string = String.valueOf(id);
            final String raw_data = record.getOriginalData();
            final String gold_standard_code = gold_standard_classification.getCode();
            final String gold_standard_scheme_label = code_label_lookup.get(gold_standard_code);
            final String output_code = classification.getCode();
            final String output_code_scheme_label = code_label_lookup.get(output_code);
            final String ancestor_distance = String.valueOf(longestMatchingPrefixLength(gold_standard_code, output_code));
            final String confidence = String.valueOf(classification.getConfidence());

            evaluation_output.addRow(id_string, raw_data, gold_standard_code, gold_standard_scheme_label, output_code, output_code_scheme_label, ancestor_distance, confidence);
        }

        persistDataSetToPath(classified_evaluation_data_path, evaluation_output);
    }

    private void populateCodeLabelLookup() throws IOException {

        if (code_label_lookup_path != null) {
            final DataSet code_label_dataset = new DataSet(Files.newBufferedReader(classified_unseen_data_path));

            for (List<String> cells : code_label_dataset.getRecords()) {
                code_label_lookup.put(cells.get(CODE_INDEX), cells.get(LABEL_INDEX));
            }
        }
    }

    public int longestMatchingPrefixLength(String one, String another) {

        return longestMatchingPrefix(one, another).length();
    }

    public String longestMatchingPrefix(String one, String another) {

        final int min_length = Math.min(one.length(), another.length());
        for (int i = 0; i < min_length; i++) {
            if (one.charAt(i) != another.charAt(i)) {
                return one.substring(0, i);
            }
        }
        return one.substring(0, min_length);
    }

    private void classifyUnseenRecords(final List<ClassifierResults> results) throws IOException {

        //TODO embed this into the steps of the last classification process.
        final ClassifierResults last_results = getLast(results);
        final Classifier classifier = last_results.getContexts().get(0).getClassifier();
        final DataSet unseen_data_set = new DataSet(Files.newBufferedReader(unseen_data_path));
        final Bucket unseen_data_bucket = new Bucket(unseen_data_set);
        final Bucket classified_unseen_data = classifier.classify(unseen_data_bucket);
        final List<String> classified_dataset_labels = Arrays.asList("ID", "RAW_DATA", "OUTPUT_CODE", "OUTPUT_CODE_SCHEME_LABEL", "CONFIDENCE");
        final DataSet classified_unseen_data_set = new DataSet(classified_dataset_labels);

        for (Record record : classified_unseen_data) {

            final Classification classification = record.getClassification();

            final String id = String.valueOf(record.getId());
            final String raw_data = record.getOriginalData();
            final String output_code = classification.getCode();
            final String output_code_scheme_label = code_label_lookup.get(output_code);
            final String confidence = String.valueOf(classification.getConfidence());

            classified_unseen_data_set.addRow(id, raw_data, output_code, output_code_scheme_label, confidence);
        }

        if (classified_unseen_data_path != null) {
            persistDataSetToPath(classified_unseen_data_path, classified_unseen_data_set);
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

    private void persistDataSetToPath(Path destination, final DataSet classified_unseen_data_set) throws IOException {

        try (final BufferedWriter out = Files.newBufferedWriter(destination)) {
            classified_unseen_data_set.print(out);
        }
    }

    private ClassifierResults getLast(final List<ClassifierResults> results) {

        return results != null && !results.isEmpty() ? results.get(results.size() - 1) : null;
    }
}
