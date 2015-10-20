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

import com.beust.jcommander.*;
import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * @author Masih Hajiarab Derkani
 */
@Parameters(optionPrefixes = "")
public abstract class LoadRecordsCommand extends Command {

    private static final Logger LOGGER = Logger.getLogger(LoadRecordsCommand.class.getName());

    @Parameter(names = "delimiter", description = "The data file delimiter character", converter = Converters.CharacterConverter.class)
    private Character delimiter = launcher.getConfiguration().getDefaultDelimiter();

    @Parameter(names = "format", description = "The format of the csv file containing the data to be loaded")
    private CsvFormatSupplier csv_format_supplier = launcher.getConfiguration().getDefaultCsvFormatSupplier();

    @Parameter(names = "skip_header", description = "Whether the CSV data file has headers.")
    private boolean skip_header_record;

    @Parameter(names = "id_column_index", description = "The zero-based index of the column containing the ID.")
    private Integer id_column_index = 0;

    @Parameter(names = "label_column_index", description = "The zero-based index of the column containing the label.")
    private Integer label_column_index = 1;

    protected final LoadCommand load_command;

    public LoadRecordsCommand(LoadCommand load_command) {

        super(load_command.launcher);
        this.load_command = load_command;
    }

    @Override
    public void run() {

        //TODO Add system logs.
        final Stream<Record> records = loadRecords();
        updateConfiguration(records);
    }

    protected abstract void updateConfiguration(final Stream<Record> records);

    protected Stream<Record> loadRecords() {

        final CSVFormat format = getCsvFormat();
        final Path source = load_command.getSource();
        final Charset charset = load_command.getCharset();

        //TODO Check if destination exists, if so override upon confirmation.
        //TODO Add system logs.

        try (final BufferedReader in = Files.newBufferedReader(source, charset)) {

            final CSVParser parser = format.parse(in);
            return StreamSupport.stream(parser.spliterator(), true).map(this::toRecord);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected CSVFormat getCsvFormat() {

        return csv_format_supplier.get().withDelimiter(delimiter).withSkipHeaderRecord(skip_header_record);
    }

    protected abstract Record toRecord(final CSVRecord record);

    protected String getLabel(final CSVRecord record) {

        return record.get(label_column_index);
    }

    protected Integer getId(final CSVRecord record) {

        return Integer.parseInt(record.get(id_column_index));
    }
}
