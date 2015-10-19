package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

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

    /** Name of record classification CLI program. */
    public static final String PROGRAM_NAME = "classi";

    /** The name of the folder that contains the persisted state of this program. */
    public static final Path CLI_HOME = Paths.get(".", Configuration.PROGRAM_NAME);
    public static final Path GOLD_STANDARD_HOME = CLI_HOME.resolve("gold_standard");
    public static final Path UNSEEN_HOME = CLI_HOME.resolve("unseen");

    private static final CsvFormatSupplier DEFAULT_CSV_FORMAT_SUPPLIER = CsvFormatSupplier.DEFAULT;
    private static final char DEFAULT_DELIMITER = DEFAULT_CSV_FORMAT_SUPPLIER.get().getDelimiter();
    private static final CharsetSupplier DEFAULT_CHARSET_SUPPLIER = CharsetSupplier.SYSTEM_DEFAULT;

    private CharsetSupplier default_charset_supplier = DEFAULT_CHARSET_SUPPLIER;
    private Character default_delimiter = DEFAULT_DELIMITER;
    private Long seed;
    private boolean proceed_on_error;
    private Path error_log;
    private Path info_log;
    private Path working_directory;
    private String name;
    private Classifier classifier;
    private SerializationFormat serialization_format;
    private CsvFormatSupplier default_csv_format_supplier = DEFAULT_CSV_FORMAT_SUPPLIER;

    private List<GoldStandard> gold_standards;
    private List<Unseen> unseens;

    public Configuration() {

        gold_standards = new ArrayList<>();
        unseens = new ArrayList<>();
    }

    public String getProgramName() {

        return PROGRAM_NAME;
    }

    public Path getGoldStandardHome() {

        return GOLD_STANDARD_HOME;
    }

    public void setWorkingDirectory(final Path working_directory) {

        this.working_directory = working_directory;
    }

    public void setName(final String name) {

        this.name = name; //TODO think about whether we need this
    }

    public void setClassifier(final Classifier classifier) {

        this.classifier = classifier;
    }

    public void setSeed(final Long seed) {

        this.seed = seed;
    }

    public void setDefaultCharsetSupplier(final CharsetSupplier default_charset_supplier) {

        this.default_charset_supplier = default_charset_supplier;
    }

    public void setDefaultDelimiter(final Character default_delimiter) {

        this.default_delimiter = default_delimiter;
    }

    public void setSerializationFormat(final SerializationFormat serialization_format) {

        this.serialization_format = serialization_format;
    }

    public SerializationFormat getSerializationFormat() {

        return serialization_format;
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

    public Path getUnseenHome() {

        return UNSEEN_HOME;
    }

    public void addUnseen(final Unseen unseen) {

        unseens.add(unseen);
    }

    public void addGoldStandard(final GoldStandard gold_standard) {

        gold_standards.add(gold_standard);

    }

    public List<GoldStandard> getGoldStandards() {

        return gold_standards;
    }

    public List<Unseen> getUnseens() {

        return unseens;
    }

    public static class Unseen {

        public static final Charset CHARSET = StandardCharsets.UTF_8;
        public static CSVFormat CSV_FORMAT = CSVFormat.RFC4180.withHeader("ID", "LABEL");

        private String name;
        private Bucket bucket;

        public Unseen(final String name) {

            this.name = name;
        }

        public String getName() {

            return name;
        }

        protected Path getPath() {

            return UNSEEN_HOME.resolve(name);
        }

        public synchronized Bucket toBucket() throws IOException {

            if (bucket == null) {

                bucket = new Bucket();
                try (final BufferedReader in = Files.newBufferedReader(getPath(), CHARSET)) {

                    final CSVParser parser = getCsvFormat().parse(in);
                    for (final CSVRecord csv_record : parser) {
                        final Record record = toRecord(csv_record);
                        bucket.add(record);
                    }
                }
            }
            return bucket;
        }

        public CSVFormat getCsvFormat() {

            return CSV_FORMAT;
        }

        protected Record toRecord(CSVRecord csv_record) {

            final int id = Integer.parseInt(csv_record.get(0));
            final String label = csv_record.get(1);

            return new Record(id, label);
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

    public static class GoldStandard extends Unseen {

        public static final CSVFormat CSV_FORMAT = CSVFormat.RFC4180.withHeader("ID", "LABEL", "CLASS");
        private double training_ratio;

        public GoldStandard(final String name, final double training_ratio) {

            super(name);
            this.training_ratio = training_ratio;
        }

        public double getTrainingRatio() {

            return training_ratio;
        }

        @Override
        public CSVFormat getCsvFormat() {

            return CSV_FORMAT;
        }

        @Override
        protected Record toRecord(final CSVRecord csv_record) {

            final int id = Integer.parseInt(csv_record.get(0));
            final String label = csv_record.get(1);
            final String clazz = csv_record.get(2);

            return new Record(id, label, new Classification(clazz, new TokenList(label), 0.0, null));
        }
    }
}
