package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

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

    public static class Unseen {

        private Path file;
        private Integer label_column_index;
        private Charset charset;
        private Character delimiter;
        private String name;
        private boolean skip_header;

        public Path getFile() {

            return file;
        }

        public void setFile(final Path file) {

            this.file = file;
        }

        public Integer getLabelColumnIndex() {

            return label_column_index;
        }

        public void setLabelColumnIndex(final Integer label_column_index) {

            this.label_column_index = label_column_index;
        }

        public Charset getCharset() {

            return charset;
        }

        public void setCharset(final Charset charset) {

            this.charset = charset;
        }

        public Character getDelimiter() {

            return delimiter;
        }

        public void setDelimiter(final Character delimiter) {

            this.delimiter = delimiter;
        }

        public String getName() {

            return name;
        }

        public void setName(final String name) {

            this.name = name;
        }

        public boolean isSkipHeader() {

            return skip_header;
        }

        public void setSkipHeader(final boolean skip_header) {

            this.skip_header = skip_header;
        }
    }

    public static class GoldStandard extends Unseen {

        private double training_ratio;
        private Integer class_column_index;

        public double getTrainingRatio() {

            return training_ratio;
        }

        public void setTrainingRatio(final double training_ratio) {

            this.training_ratio = training_ratio;
        }

        public Integer getClassColumnIndex() {

            return class_column_index;
        }

        public void setClassColumnIndex(final Integer class_column_index) {

            this.class_column_index = class_column_index;
        }
    }
}
