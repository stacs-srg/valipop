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
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * The Command Line Interface configuration.
 *
 * @author Masih Hajiarab Derkani
 */
public class Configuration {

    //TODO add parent config loading from user.home if exists.

    /** Name of record classification CLI program. */
    public static final String PROGRAM_NAME = "classi";

    /** The name of the folder that contains the persisted state of this program. */
    public static final Path CLI_HOME = Paths.get(Configuration.PROGRAM_NAME);
    public static final Path CONFIGURATION_FILE = CLI_HOME.resolve("config.json");
    public static final Path GOLD_STANDARD_HOME = CLI_HOME.resolve("gold_standard");
    public static final Path UNSEEN_HOME = CLI_HOME.resolve("unseen");
    public static final Path DICTIONARY_HOME = CLI_HOME.resolve("dictionary");
    public static final Path STOP_WORDS_HOME = CLI_HOME.resolve("stop_words");
    public static final CSVFormat RECORD_CSV_FORMAT = CSVFormat.RFC4180.withHeader("ID", "DATA", "ORIGINAL_DATA", "CODE", "CONFIDENCE", "DETAIL");
    public static final Charset RESOURCE_CHARSET = StandardCharsets.UTF_8;
    public static final CharsetSupplier DEFAULT_CHARSET_SUPPLIER = CharsetSupplier.SYSTEM_DEFAULT;
    public static final SerializationFormat DEFAULT_CLASSIFIER_SERIALIZATION_FORMAT = SerializationFormat.JAVA_SERIALIZATION;

    /** The default ratio of records to be used for training of the classifier. **/
    public static final double DEFAULT_TRAINING_RATIO = 0.8;

    /** The default ratio of records to be used for internal training of the classifier. **/
    public static final double DEFAULT_INTERNAL_TRAINING_RATIO = DEFAULT_TRAINING_RATIO;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SimpleModule classi = new SimpleModule("classi");
        classi.addSerializer(Configuration.class, new ConfigurationJsonSerializer());
        classi.addDeserializer(Configuration.class, new ConfigurationJsonDeserializer());
        MAPPER.registerModule(classi);
    }

    private static final CsvFormatSupplier DEFAULT_CSV_FORMAT_SUPPLIER = CsvFormatSupplier.DEFAULT;
    private static final char DEFAULT_DELIMITER = DEFAULT_CSV_FORMAT_SUPPLIER.get().getDelimiter();

    private CharsetSupplier default_charset_supplier = DEFAULT_CHARSET_SUPPLIER;
    private Character default_delimiter = DEFAULT_DELIMITER;
    private Long seed;
    private boolean proceed_on_error;
    private Classifier classifier;
    private ClassifierSupplier classifier_supplier;
    private SerializationFormat classifier_serialization_format = DEFAULT_CLASSIFIER_SERIALIZATION_FORMAT;
    private CsvFormatSupplier default_csv_format_supplier = DEFAULT_CSV_FORMAT_SUPPLIER;
    private Map<String, GoldStandard> gold_standards;
    private Map<String, Unseen> unseens;
    private Map<String, Dictionary> dictionaries;
    private Map<String, StopWords> stop_words;
    private Random random;
    private double default_training_ratio = DEFAULT_TRAINING_RATIO;
    private double default_internal_training_ratio = DEFAULT_INTERNAL_TRAINING_RATIO;

    public Configuration() {

        gold_standards = new LinkedHashMap<>();
        unseens = new LinkedHashMap<>();
        dictionaries = new LinkedHashMap<>();
        stop_words = new LinkedHashMap<>();

        initRandom();
    }

    private void initRandom() {random = seed == null ? new Random() : new Random(seed);}

    static Configuration load() throws IOException {

        return MAPPER.readValue(Files.newBufferedReader(CONFIGURATION_FILE), Configuration.class);
    }

    public void setSeed(final Long seed) {

        this.seed = seed;
        initRandom();
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

        return getClassifier().orElseThrow(() -> new ParameterException("The classifier is required and not set; please specify a classifier."));
    }

    public Optional<Classifier> getClassifier() {

        return classifier == null ? Optional.empty() : Optional.of(classifier);
    }

    public Bucket requireGoldStandardRecords() {

        return getGoldStandardRecords().orElseThrow(() -> new ParameterException("No gold standard record is present; please load some gold standard records."));
    }

    public Optional<Bucket> getGoldStandardRecords() {

        final Optional<Bucket> training_records = getTrainingRecords();
        final Optional<Bucket> evaluation_records = getEvaluationRecords();

        if (!training_records.isPresent() && !evaluation_records.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(training_records.orElse(new Bucket()).union(evaluation_records.orElse(new Bucket())));
    }

    public Optional<Bucket> getTrainingRecords() {

        return getGoldStandards().stream().map(Configuration.GoldStandard::getTrainingRecords).reduce(Bucket::union);
    }

    public List<GoldStandard> getGoldStandards() {

        return new ArrayList<>(gold_standards.values());
    }

    public Optional<Bucket> getEvaluationRecords() {

        return getGoldStandards().stream().map(Configuration.GoldStandard::getEvaluationRecords).reduce(Bucket::union);
    }

    public Bucket requireEvaluationRecords() {

        return getEvaluationRecords().orElseThrow(() -> new ParameterException("No evaluation record is present; please load some evaluation records."));
    }

    public Bucket requireTrainingRecords() {

        return getTrainingRecords().orElseThrow(() -> new ParameterException("No training record is present; please load some training records."));
    }

    public Bucket requireUnseenRecords() {

        return getUnseenRecords().orElseThrow(() -> new ParameterException("No unseen record is present; please load some unseen records."));
    }

    public Optional<Bucket> getUnseenRecords() {

        return getUnseens().stream().map(Configuration.Unseen::toBucket).reduce(Bucket::union);
    }

    public List<Unseen> getUnseens() {

        return new ArrayList<>(unseens.values());
    }

    public List<Bucket> requireUnseenRecordsList() {

        final List<Bucket> unseen_records_list = getUnseenRecordsList();

        if (unseen_records_list.isEmpty()) {
            throw new ParameterException("No unseen record is present; please load some unseen records.");
        }

        return unseen_records_list;
    }

    public List<Bucket> getUnseenRecordsList() {

        return getUnseens().stream().map(Configuration.Unseen::toBucket).collect(Collectors.toList());
    }

    public ClassifierSupplier getClassifierSupplier() {

        return classifier_supplier;
    }

    public void setClassifierSupplier(final ClassifierSupplier classifier_supplier) {

        this.classifier_supplier = classifier_supplier;
        classifier = classifier_supplier != null ? classifier_supplier.get() : null;
    }

    public Random getRandom() {

        return random;
    }

    public void persist() throws IOException {

        MAPPER.writeValue(Files.newOutputStream(CONFIGURATION_FILE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), this);
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

    public Dictionary newDictionary(final String name, boolean override_existing) {

        checkResourceExistence("dictionary", dictionaries, name, override_existing);

        final Dictionary dictionary = new Dictionary(name);
        dictionaries.put(name, dictionary);
        return dictionary;
    }

    private void checkResourceExistence(final String keyword, final Map<String, ? extends Resource> resources, final String name, final boolean override_existing) {

        if (override_existing && resources.containsKey(name)) {
            throw new ParameterException(String.format("A %s named %s already exists.", keyword, name));
        }
    }

    public StopWords newStopWords(final String name, boolean override_existing) {

        checkResourceExistence("stop word collection", stop_words, name, override_existing);

        final StopWords stop_word = new StopWords(name);
        stop_words.put(name, stop_word);
        return stop_word;
    }

    public Unseen newUnseen(final String name, boolean override_existing) {

        checkResourceExistence("unseen record collection", unseens, name, override_existing);

        final Unseen unseen = new Unseen(name);
        unseens.put(name, unseen);
        return unseen;
    }

    public GoldStandard newGoldStandard(final String name, double training_ratio, boolean override_existing) {

        checkResourceExistence("gold standard record collection", gold_standards, name, override_existing);

        final GoldStandard gold_standard = new GoldStandard(name, training_ratio);
        gold_standards.put(name, gold_standard);
        return gold_standard;
    }

    public Long getSeed() {

        return seed;
    }

    public boolean isProceedOnErrorEnabled() {

        return proceed_on_error;
    }

    public List<Dictionary> getDictionaries() {

        return new ArrayList<>(dictionaries.values());
    }

    public List<StopWords> getStopWords() {

        return new ArrayList<>(stop_words.values());
    }

    public void setProceedOnError(final Boolean proceed_on_error) {

        this.proceed_on_error = proceed_on_error;
    }

    abstract class Resource {

        private final String name;

        protected Resource(final String name) {

            this.name = name;
        }

        public String getName() {

            return name;
        }

        public Path getPath() {

            return getHome().resolve(name);
        }

        protected abstract Path getHome();

        public Charset getCharset() {

            return RESOURCE_CHARSET;
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            final Resource resource = (Resource) o;
            return Objects.equals(name, resource.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name);
        }
    }

    public class Dictionary extends Resource {

        Dictionary(final String name) {

            super(name);
        }

        @Override
        protected Path getHome() {

            return DICTIONARY_HOME;
        }
    }

    public class StopWords extends Dictionary {

        StopWords(final String name) {

            super(name);
        }

        @Override
        protected Path getHome() {

            return STOP_WORDS_HOME;
        }
    }

    public class Unseen extends Resource {

        protected Bucket bucket;

        Unseen(final String name) {

            super(name);
        }

        public synchronized Bucket toBucket() {

            if (bucket == null) {

                bucket = load(getPath());
            }
            return bucket;
        }

        protected Bucket load(final Path source) {

            final Bucket bucket = new Bucket();
            try (final BufferedReader in = Files.newBufferedReader(source, getCharset())) {

                final CSVParser parser = getCsvFormat().parse(in);
                for (final CSVRecord csv_record : parser) {
                    final Record record = toRecord(csv_record);
                    bucket.add(record);
                }
            }
            catch (IOException e) {
                throw new IOError(e);
            }
            return bucket;
        }

        protected CSVFormat getCsvFormat() {

            return RECORD_CSV_FORMAT;
        }

        @Override
        protected Path getHome() {

            return UNSEEN_HOME;
        }

        protected Record toRecord(CSVRecord csv_record) {

            final int id = Integer.parseInt(csv_record.get(0));
            final String label = csv_record.get(1);
            final String label_original = csv_record.get(2);
            final String code = csv_record.get(3);
            final double confidence = Double.parseDouble(csv_record.get(3));
            final String details = csv_record.get(4);

            final Classification classification = new Classification(code, new TokenList(label), confidence, details);

            return new Record(id, label, label_original, classification);
        }

        public synchronized void add(final Stream<Record> records) {

            if (bucket == null) {
                bucket = new Bucket();
            }

            bucket.add(records.collect(Collectors.toList()));
        }

        public void setBucket(Bucket bucket) {

            this.bucket = bucket;
        }
    }

    public class GoldStandard extends Unseen {

        private double training_ratio;
        private Bucket training_records;
        private Bucket evaluation_records;

        GoldStandard(final String name, final double training_ratio) {

            super(name);
            this.training_ratio = training_ratio;
        }

        public double getTrainingRatio() {

            return training_ratio;
        }

        @Override
        public synchronized Bucket toBucket() {

            if (training_records == null || evaluation_records == null) {

                training_records = load(getTrainingRecordsPath());
                evaluation_records = load(getEvaluationRecordsPath());
            }

            return training_records.union(evaluation_records);
        }

        @Override
        protected Path getHome() {

            return GOLD_STANDARD_HOME;
        }

        @Override
        public void setBucket(final Bucket bucket) {

            this.bucket = bucket;
            training_records = bucket.randomSubset(getRandom(), training_ratio);
            evaluation_records = bucket.difference(training_records);
        }

        private Path getTrainingRecordsPath() {

            return getPath().resolve("training.csv");
        }

        private Path getEvaluationRecordsPath() {

            return getPath().resolve("evaluation.csv");
        }

        public Bucket getTrainingRecords() {

            return training_records;
        }

        public Bucket getEvaluationRecords() {

            return evaluation_records;
        }
    }
}
