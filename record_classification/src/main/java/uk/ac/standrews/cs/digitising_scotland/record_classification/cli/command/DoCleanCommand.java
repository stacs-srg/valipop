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
import com.beust.jcommander.converters.*;
import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Cleans records.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = DoCleanCommand.NAME, commandDescription = "Cleans records", separators = "=")
public class DoCleanCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "clean";

    public static final String CLEAN_DESCRIPTION = "A cleaner with which to clean the data";
    public static final String CLEAN_FLAG_SHORT = "-cl";
    public static final String CLEAN_FLAG_LONG = "--cleaner";
    
    @Parameter(required = true, names = {CLEAN_FLAG_SHORT, CLEAN_FLAG_LONG}, description = CLEAN_DESCRIPTION, variableArity = true)
    private List<CleanerSupplier> cleaner_suppliers;

    @Parameter(required = true, names = {"-s", "--source"}, description = "The source file containing the records to be cleaned.", converter = PathConverter.class)
    private Path source;

    @Parameter(required = true, names = {"-d", "--destination"}, description = "The destination in which to persist the cleaned records.", converter = PathConverter.class)
    private Path destination;

    @Parameter(names = {"-c", "--charset"}, description = "The charset if source/destination file.")
    private CharsetSupplier charset_supplier = CharsetSupplier.SYSTEM_DEFAULT;

    @Parameter(names = {"-s", "--delimiter"}, description = "The delimiter character of source/destination file.")
    private Character delimiter = CSVFormat.RFC4180.getDelimiter();

    @Parameter(required = true, names = {"-col", "--sourceColumns"}, description = "The columns in the source file to clean starting from 1.", variableArity = true, validateValueWith = Validators.AtLeastOne.class)
    private List<Integer> columns;

    @Parameter(names = {"-di", "--dictionary"}, description = "The plain text file containing the dictionary of words to be used for spelling correction; one word per line", variableArity = true)
    private List<Path> dictionaries;

    @Parameter(names = {"-st", "--stopWord"}, description = "The columns in the source file to clean starting from 1.", variableArity = true)
    private List<Path> stop_words;

    /**
     * Instantiates the clean command for a given launcher.
     *
     * @param launcher the launcher to which this command belongs
     */
    public DoCleanCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final Charset charset = getCharset();
        final CSVFormat format = CSVFormat.newFormat(delimiter).withHeader();
        final Cleaner cleaner = getCombinedStringCleaner();

        try (
                        final CSVParser parser = CSVParser.parse(source.toFile(), charset, format);
                        final CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(destination), format)
        ) {
            StreamSupport.stream(parser.spliterator(), false).forEach(record -> cleanAndPrint(record, cleaner, printer));
            printer.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanAndPrint(CSVRecord record, final Cleaner cleaner, final CSVPrinter printer) {

        try {

            for (int index = 0; index < record.size(); index++) {

                final String cleaned_value = cleanValueAt(cleaner, index, record);
                printer.print(cleaned_value);
            }

            printer.println();
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("failed to persist cleaned record, with number %d at position %d", record.getRecordNumber(), record.getCharacterPosition()), e);
        }
    }

    public String cleanValueAt(final Cleaner combined, final int index, CSVRecord record) {

        final String value = record.get(index);
//        return isCleanable(index) ? combined.apply(value) : value;
        return value;
    }

    public boolean isCleanable(final int index) {return columns.contains(index);}

    private Cleaner getCombinedStringCleaner() {

        return cleaner_suppliers.stream().map(Supplier::get).reduce(Cleaner::andThen).orElseThrow(() -> new ParameterException("no cleaner is specified"));
    }

    private Charset getCharset() {

        return charset_supplier.get();
    }
}
