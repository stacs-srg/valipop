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
 * Captures common functionality in loading tabular data from file in form of {@link Record records}.
 *
 * @author Masih Hajiarab Derkani
 */
abstract class LoadRecordsCommand extends Command {

    /** The short name of the option that specifies the delimiter character in the tabular resource file to be loaded. **/
    public static final String OPTION_DELIMITER_SHORT = "-d";

    /** The long name of the option that specifies the delimiter character in the tabular resource file to be loaded. **/
    public static final String OPTION_DELIMITER_LONG = "--delimiter";

    /** The short name of the option that specifies the {@link CSVFormat format} of the tabular resource file to be loaded. **/
    public static final String OPTIONS_FORMAT_SHORT = "-f";

    /** The long name of the option that specifies the {@link CSVFormat format} of the tabular resource file to be loaded. **/
    public static final String OPTIONS_FORMAT_LONG = "--format";

    /** The short name of the option that specifies whether to skip the header record in the tabular resource file to be loaded. **/
    public static final String OPTION_SKIP_HEADER_SHORT = "-h";

    /** The long name of the option that specifies whether to skip the header record in the tabular resource file to be loaded. **/
    public static final String OPTION_SKIP_HEADER_LONG = "--skip_header";

    /** The short name of the option that specifies the index of the column that contains the ID associated to each row, starting from {@value 0}. **/
    public static final String OPTION_ID_COLUMN_INDEX_SHORT = "-ii";

    /** The long name of the option that specifies the index of the column that contains the ID associated to each row, starting from {@value 0}. **/
    public static final String OPTION_ID_COLUMN_INDEX_LONG = "--id_column_index";

    /** The short name of the option that specifies the index of the column that contains the label associated to each row, starting from {@value 0}. **/
    public static final String OPTION_LABEL_COLUMN_INDEX_SHORT = "-li";

    /** The long name of the option that specifies the index of the column that contains the label associated to each row, starting from {@value 0}. **/
    public static final String OPTION_LABEL_COLUMN_INDEX_LONG = "--label_column_index";

    /** The default index of the column that contains the ID associated to each row. **/
    public static final int DEFAULT_ID_COLUMN_INDEX = 0;

    /** The default index of the column that contains the label associated to each row. **/
    public static final int DEFAULT_LABEL_COLUMN_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(LoadRecordsCommand.class.getName());

    protected final LoadCommand load_command;

    @Parameter(names = {OPTION_DELIMITER_SHORT, OPTION_DELIMITER_LONG}, description = "The data file delimiter character", converter = Converters.CharacterConverter.class)
    private Character delimiter = launcher.getConfiguration().getDefaultDelimiter();

    @Parameter(names = {OPTIONS_FORMAT_SHORT, OPTIONS_FORMAT_LONG}, description = "The format of the csv file containing the data to be loaded")
    private CsvFormatSupplier csv_format_supplier = launcher.getConfiguration().getDefaultCsvFormatSupplier();

    @Parameter(names = {OPTION_SKIP_HEADER_SHORT, OPTION_SKIP_HEADER_LONG}, description = "Whether the CSV data file has headers.")
    private boolean skip_header_record;

    @Parameter(names = {OPTION_ID_COLUMN_INDEX_SHORT, OPTION_ID_COLUMN_INDEX_LONG}, description = "The zero-based index of the column containing the ID.")
    private Integer id_column_index = DEFAULT_ID_COLUMN_INDEX;

    @Parameter(names = {OPTION_LABEL_COLUMN_INDEX_SHORT, OPTION_LABEL_COLUMN_INDEX_LONG}, description = "The zero-based index of the column containing the label.")
    private Integer label_column_index = DEFAULT_LABEL_COLUMN_INDEX;

    /**
     * Instantiates this command as a sub command of the given load command.
     *
     * @param load_command the load command to which this command belongs.
     */
    LoadRecordsCommand(LoadCommand load_command) {

        super(load_command.launcher);
        this.load_command = load_command;
    }

    @Override
    public void run() {

        final Stream<Record> records = loadRecords();
        process(records);
    }

    /**
     * Processes loaded records.
     *
     * @param records the records to be processed
     */
    protected abstract void process(final Stream<Record> records);

    private Stream<Record> loadRecords() {

        final CSVFormat format = getCsvFormat();
        final Path source = load_command.getSource();
        final Charset charset = load_command.getCharset();

        LOGGER.finest(() -> String.format("loading records from %s, with charset %s, with format %s", source, charset, format));

        try (final BufferedReader in = Files.newBufferedReader(source, charset)) {

            final CSVParser parser = format.parse(in);
            return StreamSupport.stream(parser.spliterator(), true).map(this::toRecord);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failure while loading records", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the {@link CSVFormat format} of the tabular resource file.
     * The may be format specified via {@value #OPTIONS_FORMAT_SHORT} or {@value #OPTIONS_FORMAT_LONG} options.
     *
     * @return the {@link CSVFormat format} of the tabular resource file
     */
    public CSVFormat getCsvFormat() {

        return csv_format_supplier.get().withDelimiter(delimiter).withSkipHeaderRecord(skip_header_record);
    }

    /**
     * Converts a {@link CSVRecord tabular data record} into a {@link Record}.
     *
     * @param record the tabular data record to be converted
     * @return the converted record
     */
    protected abstract Record toRecord(final CSVRecord record);

    /**
     * Gets the label value from a {@link CSVRecord tabular data record} based on its column index.
     * The label column index may be specified via {@value #OPTION_LABEL_COLUMN_INDEX_SHORT} or {@value #OPTION_LABEL_COLUMN_INDEX_LONG} options.
     * If unspecified, the default index of {@value #DEFAULT_LABEL_COLUMN_INDEX} is used.
     *
     * @param record the record from which to extract label
     * @return the record label
     */
    protected String getLabel(final CSVRecord record) {

        return record.get(label_column_index);
    }

    /**
     * Gets the ID value from a {@link CSVRecord tabular data record} based on its column index.
     * The ID column index may be specified via {@value #OPTION_ID_COLUMN_INDEX_SHORT} or {@value #OPTION_ID_COLUMN_INDEX_LONG} options.
     * If unspecified, the default index of {@value #DEFAULT_ID_COLUMN_INDEX} is used.
     *
     * @param record the record from which to extract ID
     * @return the record ID
     */
    protected Integer getId(final CSVRecord record) {

        return Integer.parseInt(record.get(id_column_index));
    }
}
