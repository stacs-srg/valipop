package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.nio.charset.*;
import java.nio.file.*;

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

    class Unseen {

        private Path file;
        private Integer label_column_index;
        private Charset charset;
        private String delimiter;
        private String name;
        private boolean skip_first_row;
    }

    class GoldStandard extends Unseen {

        private double training_ratio;
        private Integer class_column_index;
    }
}
