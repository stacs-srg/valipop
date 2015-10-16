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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;
import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

/**
 * Command to load gold standard data from one or more files.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = LoadGoldStandardCommand.NAME, commandDescription = "Load gold standard data")
public class LoadGoldStandardCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "load_gold_standard";

    public static final String GOLD_STANDARD_DESCRIPTION = "Path to a CSV file containing the gold standard.";
    public static final String GOLD_STANDARD_FLAG = "from";
    public static final String TRAINING_RATIO_FLAG = "trainingRatio";
    public static final String CHARSET_FLAG = "charset";
    public static final String DELIMITER_FLAG = "delimiter";
    @Parameter(required = true, names = GOLD_STANDARD_FLAG, description = GOLD_STANDARD_DESCRIPTION, converter = PathConverter.class)
    private Path source;

    public static final String TRAINING_RATIO_DESCRIPTION = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).";
    @Parameter(required = true, names = TRAINING_RATIO_FLAG, description = TRAINING_RATIO_DESCRIPTION, validateValueWith = Validators.BetweenZeroAndOne.class)
    private Double training_ratio;

    public static final String CHARSET_DESCRIPTION = "The data file charset";
    @Parameter(names = CHARSET_FLAG, description = CHARSET_DESCRIPTION)
    private CharsetSupplier charset_supplier = launcher.getConfiguration().getDefaultCharsetSupplier();

    public static final String DELIMITER_DESCRIPTION = "The data file delimiter character";
    @Parameter(names = DELIMITER_FLAG, description = DELIMITER_DESCRIPTION)
    private Character delimiter = launcher.getContext().getDefaultDelimiter();

    @Parameter(names = "format", description = "The format of the csv file containing the data to be loaded")
    private CsvFormatSupplier csv_format_supplier = launcher.getConfiguration().getDefaultCsvFormatSupplier();

    @Parameter(names = "skip_header", description = "Whether the CSV data file has headers.")
    private boolean skip_header_record;

    @Parameter(names = "name", description = "The name of the data file.")
    private String name;

    @Parameter(names = "label_column_index", description = "The index of the column containing the gold standard label, starting from zero.")
    private Integer label_column_index = 0;

    @Parameter(names = "class_column_index", description = "The index of the column containing the gold standard class associated to each label, starting from zero.")
    private Integer class_column_index = 1;

    public LoadGoldStandardCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();
        final Path gold_standard_home = configuration.getGoldStandardHome();
        final CSVFormat format = getCsvFormat();
        final Path destination = gold_standard_home.resolve(getGoldStandardName());
        final Charset charset = charset_supplier.get();

        //TODO: Check if destination exists, if so override upon confirmation.

        try (
                        final BufferedReader in = Files.newBufferedReader(source, charset);
                        final BufferedWriter out = Files.newBufferedWriter(destination, charset)
        ) {

            assureDirectoryExists(gold_standard_home);

            final CSVParser parser = format.parse(in);
            final CSVPrinter printer = format.print(out);

            parser.forEach(record -> loadGoldStandardRecord(printer, record));

            out.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

//        final ClassificationContext context = launcher.getContext();
//        new LoadTrainingAndEvaluationRecordsByRatioStep(gold_standard, training_ratio, charset, delimiter).perform(context);
    }

    private void loadGoldStandardRecord(final CSVPrinter printer, final CSVRecord record) {

        final String label = record.get(label_column_index);
        final String clazz = record.get(class_column_index);

        try {
            loadClassForLabel(printer, clazz, label);
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("failed to load gold standard record, no: %d, at: %d", record.getRecordNumber(), record.getCharacterPosition()), e);
        }
    }

    private void loadClassForLabel(final CSVPrinter printer, final String clazz, final String label) throws IOException {

        printer.printRecord(label, clazz);
    }

    public String getGoldStandardName() {

        return name == null ? source.getFileName().toString() : name;
    }

    private CSVFormat getCsvFormat() {

        return csv_format_supplier.get().withDelimiter(delimiter).withSkipHeaderRecord(skip_header_record);
    }

    private void assureDirectoryExists(final Path directory) throws IOException {

        if (!Files.isDirectory(directory)) {
            final Path directories = Files.createDirectories(directory);
            if (!Files.isDirectory(directories)) {
                throw new IOException("failed to create directory");
            }
        }
    }
}
