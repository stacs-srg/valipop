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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import com.beust.jcommander.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.*;

import org.apache.commons.csv.*;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * The Command Line Interface configuration.
 *
 * @author Masih Hajiarab Derkani
 */
public class Configuration extends ClassificationContext {

    //TODO add parent config loading from user.home if exists.

    /** Name of record classification CLI program. */
    public static final String PROGRAM_NAME = "classli";

    /** The name of the folder that contains the persisted state of this program. */
    public static final String HOME_NAME = "." + Configuration.PROGRAM_NAME;

    /** The name of the resource bundle containing the command line interface messages. */
    public static final String RESOURCE_BUNDLE_NAME = "uk.ac.standrews.cs.digitising_scotland.record_classification.cli.CLIMessages";

    /** Format of the {@link Record records} stored in a CSV file by the command line interface. */
    public static final CSVFormat RECORD_CSV_FORMAT = CSVFormat.RFC4180.withHeader("ID", "DATA", "ORIGINAL_DATA", "CODE", "CONFIDENCE", "DETAIL").withSkipHeaderRecord();

    /** The character encoding by which the state of the command line interface is persisted. */
    public static final Charset RESOURCE_CHARSET = StandardCharsets.UTF_8;

    /** */
    public static final CharsetSupplier DEFAULT_CHARSET_SUPPLIER = CharsetSupplier.SYSTEM_DEFAULT;
    public static final SerializationFormat DEFAULT_CLASSIFIER_SERIALIZATION_FORMAT = SerializationFormat.JAVA_SERIALIZATION;

    /** The default working directory of the command line interface. */
    public static final Path DEFAULT_WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"));

    /** The default ratio of records to be used for training of the classifier. **/
    public static final double DEFAULT_TRAINING_RATIO = 0.8;

    /** The default ratio of records to be used for internal training of the classifier. **/
    public static final double DEFAULT_INTERNAL_TRAINING_RATIO = DEFAULT_TRAINING_RATIO;

    protected static final CsvFormatSupplier DEFAULT_CSV_FORMAT_SUPPLIER = CsvFormatSupplier.DEFAULT;
    protected static final Character DEFAULT_DELIMITER = DEFAULT_CSV_FORMAT_SUPPLIER.get().getDelimiter();
    protected static final LogLevelSupplier DEFAULT_LOG_LEVEL_SUPPLIER = LogLevelSupplier.INFO;

    private static final long serialVersionUID = 5386411103557347275L;
    private static final Handler HANDLER = new CLIConsoleHandler();
    private static final String CONFIG_FILE_NAME = "config.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SimpleModule classi = new SimpleModule(PROGRAM_NAME);
        classi.addSerializer(Configuration.class, new ConfigurationJsonSerializer());
        classi.addDeserializer(Configuration.class, new ConfigurationJsonDeserializer());
        MAPPER.registerModule(classi);

        final String logger_name = Launcher.class.getPackage().getName();
        final Logger parent_logger = CLILogManager.CLILogger.getLogger(logger_name);
        parent_logger.setUseParentHandlers(false);
        parent_logger.addHandler(HANDLER);
    }

    private CharsetSupplier default_charset_supplier = DEFAULT_CHARSET_SUPPLIER;
    private Character default_delimiter = DEFAULT_DELIMITER;
    private double default_training_ratio = DEFAULT_TRAINING_RATIO;
    private double default_internal_training_ratio = DEFAULT_INTERNAL_TRAINING_RATIO;
    private CsvFormatSupplier default_csv_format_supplier = DEFAULT_CSV_FORMAT_SUPPLIER;
    private LogLevelSupplier default_log_level_supplier = DEFAULT_LOG_LEVEL_SUPPLIER;
    private Long seed;
    private ClassifierSupplier classifier_supplier;
    private SerializationFormat classifier_serialization_format = DEFAULT_CLASSIFIER_SERIALIZATION_FORMAT;
    private Level log_level;
    private transient Path working_directory = DEFAULT_WORKING_DIRECTORY;

    public Configuration() {

        this(DEFAULT_WORKING_DIRECTORY);
    }

    public Configuration(Path working_directory) {

        this.working_directory = working_directory;
    }

    public static Configuration load() throws IOException {

        return load(DEFAULT_WORKING_DIRECTORY);
    }

    public static Configuration load(Path working_directory) {

        final Path home = getHome(working_directory);
        final Path config_file = getConfigurationFile(home);
        try (final BufferedReader in = Files.newBufferedReader(config_file)) {
            final Configuration configuration = MAPPER.readValue(in, Configuration.class);
            configuration.setWorkingDirectory(working_directory);
            return configuration;
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from" + home + ", cause: " + e.getMessage(), e);
        }
    }

    private static Path getHome(final Path working_directory) {return working_directory.resolve(HOME_NAME);}

    private static Path getConfigurationFile(final Path home) {return home.resolve(CONFIG_FILE_NAME);}

    public static void persistBucketAsCSV(Bucket bucket, Path destination, CSVFormat format, Charset charset) throws IOException {

        if (bucket != null) {

            try (final BufferedWriter out = Files.newBufferedWriter(destination, charset)) {

                final CSVPrinter printer = format.print(out);
                for (Record record : bucket) {
                    final Classification classification = record.getClassification();
                    printer.print(record.getId());
                    printer.print(record.getData());
                    printer.print(record.getOriginalData());
                    printer.print(classification.getCode());
                    printer.print(classification.getConfidence());
                    printer.print(classification.getDetail());
                    printer.println();
                }
            }
        }
    }

    public static Bucket loadBucket(final Path source) throws IOException {

        final Bucket bucket = new Bucket();

        try (final BufferedReader in = Files.newBufferedReader(source, RESOURCE_CHARSET)) {
            final CSVParser parser = RECORD_CSV_FORMAT.parse(in);
            StreamSupport.stream(parser.spliterator(), false).map(Configuration::toRecord).forEach(bucket::add);
        }
        return bucket;
    }

    public static Record toRecord(final CSVRecord csv_record) {

        final int id = Integer.parseInt(csv_record.get(0));
        final String label = csv_record.get(1);
        final String label_original = csv_record.get(2);
        final String code = csv_record.get(3);
        final double confidence = Double.parseDouble(csv_record.get(4));
        final String details = csv_record.get(5);

        final Classification classification = new Classification(code, new TokenList(label), confidence, details);

        return new Record(id, label, label_original, classification);
    }

    static boolean exists(final Path working_directory) {

        return Files.isRegularFile(Configuration.getConfigurationFile(getHome(working_directory)));
    }

    @Override
    protected void setClassifier(final Classifier classifier) {

        this.classifier = classifier;
    }

    public Path getWorkingDirectory() {

        return working_directory;
    }

    public void setWorkingDirectory(final Path working_directory) {

        Objects.requireNonNull(working_directory);
        this.working_directory = working_directory;
    }

    protected Path getEvaluationRecordsPath() {

        return getHome().resolve("evaluation.csv");
    }

    public Path getHome() {

        return getHome(working_directory);
    }

    protected Path getTrainingRecordsPath() {

        return getHome().resolve("training.csv");
    }

    protected Path getUnseenRecordsPath() {

        return getHome().resolve("unseen.csv");
    }

    protected Path getClassifiedUnseenRecordsPath() {

        return getHome().resolve("classified_unseen.csv");
    }

    protected Path getClassifiedEvaluationRecordsPath() {

        return getHome().resolve("classified_evaluation.csv");
    }

    protected Path getConfusionMatrixPath() {

        return getHome().resolve("confusion_matrix.object");
    }

    protected Path getClassificationMetricsPath() {

        return getHome().resolve("classification_metrics.object");
    }

    public SerializationFormat getClassifierSerializationFormat() {

        return classifier_serialization_format;
    }

    public void setClassifierSerializationFormat(final SerializationFormat serialization_format) {

        this.classifier_serialization_format = serialization_format;
    }

    public CsvFormatSupplier getDefaultCsvFormatSupplier() {

        return default_csv_format_supplier;
    }

    public void setDefaultCsvFormatSupplier(final CsvFormatSupplier default_csv_format_supplier) {

        this.default_csv_format_supplier = default_csv_format_supplier;
    }

    public CharsetSupplier getDefaultCharsetSupplier() {

        return default_charset_supplier;
    }

    public void setDefaultCharsetSupplier(final CharsetSupplier default_charset_supplier) {

        this.default_charset_supplier = default_charset_supplier;
    }

    public Character getDefaultDelimiter() {

        return default_delimiter;
    }

    public void setDefaultDelimiter(final Character default_delimiter) {

        this.default_delimiter = default_delimiter;
    }

    public Classifier requireClassifier() {

        return getClassifierOptional().orElseThrow(() -> new ParameterException("The classifier is required and not set; please specify a classifier."));
    }

    public Optional<Classifier> getClassifierOptional() {

        return classifier == null ? Optional.empty() : Optional.of(classifier);
    }

    public Bucket requireGoldStandardRecords() {

        return getGoldStandardRecordsOptional().orElseThrow(() -> new ParameterException("No gold standard record is present; please load some gold standard records."));
    }

    public Bucket requireEvaluationRecords() {

        return getEvaluationRecordsOptional().orElseThrow(() -> new ParameterException("No evaluation record is present; please load some evaluation records."));
    }

    public Bucket requireTrainingRecords() {

        return getTrainingRecordsOptional().orElseThrow(() -> new ParameterException("No training record is present; please load some training records."));
    }

    public Bucket requireUnseenRecords() {

        return getUnseenRecordsOptional().orElseThrow(() -> new ParameterException("No unseen record is present; please load some unseen records."));
    }

    public ClassifierSupplier getClassifierSupplier() {

        return classifier_supplier;
    }

    public void setClassifierSupplier(final ClassifierSupplier classifier_supplier) {

        this.classifier_supplier = classifier_supplier;
        resetClassifier();
    }

    private void resetClassifier() {

        if (classifier_supplier != null) {
            classifier = classifier_supplier.get();
        }
    }

    public void persist() throws IOException {

        try (final OutputStream out = Files.newOutputStream(getConfigurationFile(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(out, this);
            out.flush();
        }
    }

    public Path getConfigurationFile() {

        return getConfigurationFile(getHome());
    }

    public double getDefaultInternalTrainingRatio() {

        return default_internal_training_ratio;
    }

    public void setDefaultInternalTrainingRatio(final double default_internal_training_ratio) {

        this.default_internal_training_ratio = default_internal_training_ratio;
    }

    public double getDefaultTrainingRatio() {

        return default_training_ratio;
    }

    public void setDefaultTrainingRatio(final double default_training_ratio) {

        this.default_training_ratio = default_training_ratio;
    }

    public Long getSeed() {

        return seed;
    }

    public void setSeed(final Long seed) {

        this.seed = seed;
        resetRandom();
    }

    private void resetRandom() {

        if (isSeeded()) {
            setRandom(new Random(seed));
        }
    }

    public boolean isSeeded() {

        return seed != null;
    }

    public Level getLogLevel() {

        return log_level;
    }

    void setLogLevel(final Level log_level) {

        this.log_level = log_level;
        HANDLER.setLevel(log_level);

        //TODO add file handler for error.log
    }

    public LogLevelSupplier getDefaultLogLevelSupplier() {

        return default_log_level_supplier;
    }

    public void setDefaultLogLevelSupplier(final LogLevelSupplier default_log_level_supplier) {

        this.default_log_level_supplier = default_log_level_supplier;
        setLogLevel(default_log_level_supplier.get());
    }
}
