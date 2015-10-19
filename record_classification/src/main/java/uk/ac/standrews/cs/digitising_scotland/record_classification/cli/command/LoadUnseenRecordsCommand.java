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
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = LoadUnseenRecordsCommand.NAME, commandDescription = "Load unseen records")
public class LoadUnseenRecordsCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "load_unseen";
    public static final String DATA_DESCRIPTION = "Path to a CSV file containing the data to be loaded.";
    public static final String DATA_FLAG_SHORT = "from";
    public static final String CHARSET_DESCRIPTION = "The data file charset";
    public static final String CHARSET_FLAG = "charset";
    public static final String DELIMITER_DESCRIPTION = "The data file delimiter character";
    public static final String DELIMITER_FLAG = "delimiter";

    private static final Logger LOGGER = Logger.getLogger(LoadUnseenRecordsCommand.class.getName());

    @Parameter(required = true, names = DATA_FLAG_SHORT, description = DATA_DESCRIPTION, converter = PathConverter.class)
    private Path source;
    @Parameter(names = CHARSET_FLAG, description = CHARSET_DESCRIPTION)
    private CharsetSupplier charset_supplier = launcher.getConfiguration().getDefaultCharsetSupplier();
    @Parameter(names = DELIMITER_FLAG, description = DELIMITER_DESCRIPTION)
    private Character delimiter = launcher.getContext().getDefaultDelimiter();

    @Parameter(names = "format", description = "The format of the csv file containing the data to be loaded")
    private CsvFormatSupplier csv_format_supplier = launcher.getConfiguration().getDefaultCsvFormatSupplier();

    @Parameter(names = "skip_header", description = "Whether the CSV data file has headers.")
    private boolean skip_header_record;

    @Parameter(names = "name", description = "The name of the data file.")
    private String name;

    @Parameter(names = "id_column_index", description = "The zero-based index of the column containing the ID.")
    private Integer id_column_index = 0;

    @Parameter(names = "label_column_index", description = "The zero-based index of the column containing the label.")
    private Integer label_column_index = 1;

    public LoadUnseenRecordsCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        //TODO Add system logs.
        final Stream<Record> records = loadRecords();
        updateConfiguration(records);
    }

    protected void updateConfiguration(final Stream<Record> records) {

        final Configuration configuration = launcher.getConfiguration();
        final Configuration.Unseen unseen = new Configuration.Unseen(name);
        unseen.add(records);
        configuration.addUnseen(unseen);
    }

    protected Stream<Record> loadRecords() {

        final Configuration configuration = launcher.getConfiguration();

        final CSVFormat format = getCsvFormat();
        final Path destination = getDestination(configuration);
        final Path source = getSource();
        final Charset charset = getCharset();

        //TODO Check if destination exists, if so override upon confirmation.
        //TODO Add system logs.

        try (final BufferedReader in = Files.newBufferedReader(source, charset)) {

            assureDirectoryExists(destination.getParent());
            final CSVParser parser = format.parse(in);
            return StreamSupport.stream(parser.spliterator(), true).map(this::toRecord);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getDestination(final Configuration configuration) {

        return getDataHome(configuration).resolve(getSourceName());
    }

    protected String getSourceName() {

        return name == null ? getSource().getFileName().toString() : name;
    }

    protected Path getSource() {

        return source;
    }

    protected Path getDataHome(final Configuration configuration) {

        return configuration.getUnseenHome();
    }

    private void assureDirectoryExists(final Path directory) throws IOException {

        if (!Files.isDirectory(directory)) {
            final Path directories = Files.createDirectories(directory);
            if (!Files.isDirectory(directories)) {
                throw new IOException("failed to create directory");
            }
        }
    }

    protected Record toRecord(final CSVRecord record) {

        final Integer id = getId(record);
        final String label = getLabel(record);

        return new Record(id, label);
    }

    protected String getLabel(final CSVRecord record) {

        return record.get(getLabelColumnIndex());
    }

    protected Integer getId(final CSVRecord record) {

        return Integer.parseInt(record.get(getIdColumnIndex()));
    }

    protected int getIdColumnIndex() {

        return id_column_index;
    }

    protected int getLabelColumnIndex() {

        return label_column_index;
    }

    protected Charset getCharset() {

        return charset_supplier.get();
    }

    protected CSVFormat getCsvFormat() {

        return csv_format_supplier.get().withDelimiter(delimiter).withSkipHeaderRecord(skip_header_record);
    }
}
