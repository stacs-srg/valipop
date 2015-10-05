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
import org.apache.commons.math.stat.descriptive.*;
import uk.ac.standrews.cs.classification_schemes.hisco.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.*;
import uk.ac.standrews.cs.util.dataset.*;
import uk.ac.standrews.cs.util.tables.*;
import uk.ac.standrews.cs.util.tools.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author masih
 */
public class ExperimentCLI extends Experiment {

    private static final int CODE_INDEX = 2;
    private static final int LABEL_INDEX = 1;
    private static final int MAX_CODING_SCHEME_LENGTH = 5;
    private static final HiscoScheme HISCO_SCHEME = new HiscoScheme();
    public static final List<String> THREE_COLUMN_DATASET = Arrays.asList("id", "title", "code");

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
            persistClassificationMetricsPerGroup(results, getHiscoMajorGroupCodes(), "major");
            persistClassificationMetricsPerGroup(results, getHiscoMinorGroupCodes(), "minor");
        }

        if (unseen_data_path != null) {
            classifyUnseenRecords(results);
        }

        printSummarisedResults(results);

        return null; //void callable
    }

    private void persistClassificationMetricsPerGroup(final List<ClassifierResults> results, Stream<HiscoGroup> groups, final String output_suffix) {

        final DataSet per_group_metrics = new DataSet(Arrays.asList("hisco_group_code", "hisco_group_title", "macro-precision", "macro-recall", "macro-F1", "micro-precision/recall"));

        groups.forEach(group -> {

            String group_code = group.getNumericalCode();

            final Stream<ClassificationMetrics> group_classification_metrics = getClassificationMetricsGroupByCodePrefix(results, group_code);

            final List<List<Double>> metrics_values = group_classification_metrics.map(metric -> Arrays.asList(metric.getMacroAveragePrecision(), metric.getMacroAverageRecall(), metric.getMacroAverageF1(), metric.getMicroAveragePrecision())).collect(Collectors.toList());

            final List<Double> means = new Means(metrics_values).getResults();
            final List<Double> intervals = new ConfidenceIntervals(metrics_values).getResults();

            final List<String> row = new ArrayList<>();
            row.add(group_code);
            row.add(group.getTitle());
            for (int i = 0; i < means.size(); i++) {
                row.add(String.format("%.2f ± %.2f", means.get(i), intervals.get(i)));
            }

            per_group_metrics.addRow(row);

        });

        final Path destination = Paths.get(classified_evaluation_data_path.toString() + ".per_hisco_group_" + output_suffix);
        try {
            persistDataSetToPath(destination, per_group_metrics);
        }
        catch (IOException e) {
            throw new RuntimeException("failed to persist classification metrics per group: " + output_suffix, e);
        }
    }

    private Stream<ClassificationMetrics> getClassificationMetricsGroupByCodePrefix(final List<ClassifierResults> results_list, final String code_prefix) {

        return results_list.get(0).getContexts().stream().map(context -> {

            final DataSet gold_standard_records = getGoldStandardRecords(context);
            final DataSet evaluation_classified_records = getClassifiedEvaluationRecords(context);

            final Set<String> gold_standard_record_ids_in_group = getIdOfRecordsWithMatchingCodePrefix(code_prefix, gold_standard_records);
            final DataSet evaluation_classified_records_in_group = getSubsetById(evaluation_classified_records, gold_standard_record_ids_in_group);

            final StrictConfusionMatrix matrix = new StrictConfusionMatrix(evaluation_classified_records_in_group, gold_standard_records, new ConsistentCodingChecker());

            return new ClassificationMetrics(matrix);
        });
    }

    private DataSet getSubsetById(final DataSet evaluation_classified_records, final Set<String> gold_standard_record_ids_in_group) {

        final DataSet evaluation_classified_records_in_group = new DataSet(THREE_COLUMN_DATASET);
        evaluation_classified_records.getRecords().stream().filter(record -> gold_standard_record_ids_in_group.contains(getRecordId(record))).forEach(evaluation_classified_records_in_group::addRow);
        return evaluation_classified_records_in_group;
    }

    private Set<String> getIdOfRecordsWithMatchingCodePrefix(final String major_group_code, final DataSet gold_standard_records) {return getRecordsWithMatchingCodePrefix(gold_standard_records, major_group_code, 2).getRecords().stream().map(this::getRecordId).collect(Collectors.toSet());}

    private DataSet getClassifiedEvaluationRecords(final ClassificationContext context) {return context.getConfusionMatrix().getClassifiedRecords().toDataSet2(THREE_COLUMN_DATASET);}

    private String getRecordId(final List<String> record) {return record.get(0);}

    private static DataSet getRecordsWithMatchingCodePrefix(final DataSet dataSet, final String prefix, final int code_column_index) {

        final DataSet matching_code_prefix = new DataSet(dataSet.getColumnLabels());

        dataSet.getRecords().stream().filter(record -> record.get(code_column_index).startsWith(prefix)).forEach(matching_code_prefix::addRow);

        return matching_code_prefix;
    }

    private DataSet getGoldStandardRecords(final ClassificationContext context) {

        final Bucket training_records = context.getTrainingRecords();
        final Bucket evaluation_records = context.getEvaluationRecords();
        final Bucket gold_standard_records = training_records.union(evaluation_records);
        return gold_standard_records.toDataSet2(THREE_COLUMN_DATASET);
    }

    private static Stream<HiscoGroup> getHiscoMajorGroupCodes() {

        return HISCO_SCHEME.getMajorGroups();
    }

    private static Stream<HiscoGroup> getHiscoMinorGroupCodes() {

        return HISCO_SCHEME.getMinorGroups();
    }

    private void persistClassifiedEvaluationRecords(final List<ClassifierResults> results) throws IOException {

        final AtomicInteger repetition_count = new AtomicInteger();
        results.forEach(results1 -> {
            try {
                persistClassifiedEvaluationRecords(results1, Paths.get(classified_evaluation_data_path.toString() + ".all_rep_" + repetition_count.getAndIncrement()));
            }
            catch (IOException e) {
                throw new RuntimeException("failed to persist classified evaluation records", e);
            }
        });
    }

    private void persistClassifiedEvaluationRecords(ClassifierResults results, Path destination) throws IOException {

        final ClassificationContext context = results.getContexts().get(0);
        final Bucket evaluation_records = context.getEvaluationRecords();
        final Bucket evaluation_classified_records = context.getConfusionMatrix().getClassifiedRecords();
        final Stream<Record> evaluation_records_stream = StreamSupport.stream(evaluation_records.spliterator(), false);
        final Map<Integer, Record> evaluation_records_map = evaluation_records_stream.collect(Collectors.toMap(Record::getId, record -> record));

        final DataSet evaluation_output = new DataSet(Arrays.asList("ID", "RAW_DATA", "GOLD_STANDARD_CODE", "GOLD_STANDARD_SCHEME_LABEL", "OUTPUT_CODE", "OUTPUT_CODE_SCHEME_LABEL", "ANCESTOR_DISTANCE", "CONFIDENCE"));
        for (Record record : evaluation_classified_records) {

            final int id = record.getId();
            final Classification classification = record.getClassification();

            final Record gold_standard_record = evaluation_records_map.get(id);
            final Classification gold_standard_classification = gold_standard_record.getClassification();

            final String id_string = String.valueOf(id);
            final String raw_data = record.getOriginalData();
            final String gold_standard_code = gold_standard_classification.getCode();
            final String gold_standard_scheme_label = getCodingSchemeLable(gold_standard_code);
            final String output_code = classification.getCode();
            final String output_code_scheme_label = getCodingSchemeLable(output_code);
            final String ancestor_distance = String.valueOf(MAX_CODING_SCHEME_LENGTH - longestMatchingPrefixLength(gold_standard_code, output_code));
            final String confidence = String.format("%.2f", classification.getConfidence());

            evaluation_output.addRow(id_string, raw_data, gold_standard_code, gold_standard_scheme_label, output_code, output_code_scheme_label, ancestor_distance, confidence);
        }

        persistDataSetToPath(destination, evaluation_output);
    }

    private String getCodingSchemeLable(final String code) {return code_label_lookup.getOrDefault(code, "NON_ROOT");}

    private void populateCodeLabelLookup() throws IOException {

        if (code_label_lookup_path != null) {
            final DataSet code_label_dataset = new DataSet(Files.newBufferedReader(code_label_lookup_path));

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
            final String output_code_scheme_label = getCodingSchemeLable(output_code);
            final String confidence = String.format("%.2f", classification.getConfidence());

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
