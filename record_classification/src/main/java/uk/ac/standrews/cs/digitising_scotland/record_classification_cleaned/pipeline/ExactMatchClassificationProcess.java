/*
 * Copyright 2014 Digitising Scotland project:
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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ExactMatchPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.IPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PipelineUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.tools.configuration.MachineLearningConfiguration;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.FileComparisonWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.MetricsWriter;

import java.io.File;
import java.io.IOException;

public class ExactMatchClassificationProcess implements ClassificationProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactMatchClassificationProcess.class);
    private static final String USAGE_TEXT = "usage: $" + ExactMatchClassificationProcess.class.getSimpleName() + "    <trainingDataFile>  <propertiesFile>  <trainingRatio>";

    private static final int TRAINING_DATA_FILE_ARG_POS = 0;
    private static final int PROPERTIES_FILE_ARG_POS = 1;
    private static final int TRAINING_RATIO_ARG_POS = 2;

    String experimentalFolderName;
    File training_data_file;
    double training_ratio;
    CodeDictionary code_dictionary;

    /**
     * Entry method for training and classifying a batch of records into
     * multiple codes.
     *
     * @param args <file1> training file <file2> file to classify
     * @throws Exception If exception occurs
     */
    public static void main(final String[] args) throws Exception {

        new ExactMatchClassificationProcess(args).performClassification();
    }

    public ExactMatchClassificationProcess(String[] args) throws IOException, InvalidArgException {

        LOGGER.info("Running with args: {}", (Object) args);

         experimentalFolderName = PipelineUtils.setupExperimentalFolders("ExactMatchClassifierDevelopment");

         training_data_file = getTrainingDataFileFromArgs(args);
         training_ratio = parseTrainingRatio(args);

        File code_dictionary_file = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
         code_dictionary = new CodeDictionary(code_dictionary_file);
    }

    public Bucket performClassification() throws Exception {

        Bucket all_records = new Bucket(training_data_file, code_dictionary);

        Bucket training_bucket = new Bucket();
        Bucket evaluation_bucket = new Bucket();

        randomlyAssignToTrainingAndEvaluation(all_records, training_bucket, evaluation_bucket, training_ratio);

        LOGGER.info("********** Training Classifiers **********");

        ExactMatchClassifier exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(training_bucket);

        IPipeline exactMatchPipeline = new ExactMatchPipeline(exactMatchClassifier);

        Bucket notExactMatched = exactMatchPipeline.classify(evaluation_bucket);
        Bucket successfullyExactMatched = exactMatchPipeline.getSuccessfullyClassified();
        Bucket uniqueRecordsExactMatched = BucketFilter.uniqueRecordsOnly(successfullyExactMatched);

        LOGGER.info("Exact Matched Bucket Size: " + successfullyExactMatched.size());
        LOGGER.info("Unique Exact Matched Bucket Size: " + uniqueRecordsExactMatched.size());

        Bucket allRecords = BucketUtils.getUnion(successfullyExactMatched, notExactMatched);
        assert (allRecords.size() == evaluation_bucket.size());

        writeRecords(experimentalFolderName, allRecords);

        writeComparisonFile(experimentalFolderName, allRecords);

        LOGGER.info("********** Output Stats **********");

        CodeIndexer code_indexer = new CodeIndexer(all_records);
        printAllStats(experimentalFolderName, code_indexer, allRecords, "allRecords");
        printAllStats(experimentalFolderName, code_indexer, successfullyExactMatched, "exactMatched");

        return allRecords;
    }

    private void printAllStats(final String experimentalFolderName, final CodeIndexer codeIndex, final Bucket bucket, final String identifier) throws IOException {

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(bucket);

        LOGGER.info("All Records");
        LOGGER.info("All Records Bucket Size: " + bucket.size());
        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(bucket, codeIndex), codeIndex);
        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(bucket, codeMetrics);
        MetricsWriter metricsWriter = new MetricsWriter(accuracyMetrics, experimentalFolderName, codeIndex);
        metricsWriter.write(identifier, "nonUniqueRecords");
        accuracyMetrics.prettyPrint("AllRecords");

        LOGGER.info("Unique Only");
        LOGGER.info("Unique Only  Bucket Size: " + uniqueRecordsOnly.size());

        CodeMetrics codeMetrics1 = new CodeMetrics(new StrictConfusionMatrix(uniqueRecordsOnly, codeIndex), codeIndex);
        accuracyMetrics = new ListAccuracyMetrics(uniqueRecordsOnly, codeMetrics1);
        accuracyMetrics.prettyPrint("Unique Only");
        metricsWriter = new MetricsWriter(accuracyMetrics, experimentalFolderName, codeIndex);
        metricsWriter.write(identifier, "uniqueRecords");
        accuracyMetrics.prettyPrint("UniqueRecords");
    }

    private void writeComparisonFile(final String experimentalFolderName, final Bucket allClassifed) throws IOException {

        final String comparisonReportPath = "/Data/" + "MachineLearning" + "/comaprison.txt";
        final File outputPath2 = new File(experimentalFolderName + comparisonReportPath);
        Files.createParentDirs(outputPath2);

        final FileComparisonWriter comparisonWriter = new FileComparisonWriter(outputPath2, "\t");
        for (final Record record : allClassifed) {
            comparisonWriter.write(record);
        }
        comparisonWriter.close();
    }

    private void writeRecords(final String experimentalFolderName, final Bucket allClassifed) throws IOException {

        final String nrsReportPath = "/Data/" + "MachineLearning" + "/NRSData.txt";
        final File outputPath = new File(experimentalFolderName + nrsReportPath);
        Files.createParentDirs(outputPath);
        final DataClerkingWriter writer = new DataClerkingWriter(outputPath);
        for (final Record record : allClassifed) {
            writer.write(record);
        }
        writer.close();
    }

    private static File getTrainingDataFileFromArgs(final String[] args) throws InvalidArgException {

        if (args.length <= TRAINING_DATA_FILE_ARG_POS) {
            throw new InvalidArgException("training data file argument missing");
        }

        File training_data_file = new File(args[TRAINING_DATA_FILE_ARG_POS]);

        if (!training_data_file.exists()) {
            throw new InvalidArgException(training_data_file.getAbsolutePath() + " does not exist");
        }

        return training_data_file;
    }

    private void loadMachineLearningPropertiesFromArgs(String[] args) throws InvalidArgException {

        if (args.length <= PROPERTIES_FILE_ARG_POS) {
            throw new InvalidArgException("properties file argument missing");
        }

        File properties_file = new File(args[PROPERTIES_FILE_ARG_POS]);

        if (properties_file.exists()) {
            MachineLearningConfiguration.loadProperties(properties_file);
        } else {
            LOGGER.info("supplied properties file does not exist, using default properties: " + MachineLearningConfiguration.getDefaultPropertiesPath());
        }
    }

    private static double parseTrainingRatio(final String[] args) throws InvalidArgException {

        // TODO rewrite args handling using Apache Commons CLI2

        if (args.length <= TRAINING_RATIO_ARG_POS) {
            throw new InvalidArgException("training ratio argument missing");
        }

        String training_ratio_arg = args[TRAINING_RATIO_ARG_POS];

        try {
            double training_ratio = Double.valueOf(training_ratio_arg);
            if (training_ratio > 0 && training_ratio < 1) {
                return training_ratio;
            }
            throw new InvalidArgException("invalid training ratio: " + training_ratio);

        } catch (NumberFormatException e) {
            throw new InvalidArgException("invalid training ratio: " + training_ratio_arg);
        }
    }

    private void randomlyAssignToTrainingAndEvaluation(final Bucket bucket, Bucket training_bucket, Bucket evaluation_bucket, final double trainingRatio) {

        for (Record record : bucket) {
            if (Math.random() < trainingRatio) {
                training_bucket.addRecordToBucket(record);
            } else {
                evaluation_bucket.addRecordToBucket(record);
            }
        }
    }

}
