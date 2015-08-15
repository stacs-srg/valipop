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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.CleanerSupplier;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.InitCommand;
import uk.ac.standrews.cs.util.tools.Logging;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.Step;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.LoadStep;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Captures the common functionality among the command-line interface commands.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public abstract class Command implements Callable<Void>, Step {

    private static final long serialVersionUID = -2176702491500665712L;

    protected static final String PROCESS_NAME = "classification_process";

    private static final String NAME_DESCRIPTION = "The name of the classification process";
    private static final String NAME_FLAG_SHORT = "-n";
    private static final String NAME_FLAG_LONG = "--name";

    protected static final String CHARSET_DESCRIPTION = "The data file charset";
    protected static final String CHARSET_FLAG_SHORT = "-ch";
    protected static final String CHARSET_FLAG_LONG = "--charset";

    protected static final String DELIMITER_DESCRIPTION = "The data file delimiter character";
    protected static final String DELIMITER_FLAG_SHORT = "-dl";
    protected static final String DELIMITER_FLAG_LONG = "--delimiter";

    public static final String CLEAN_DESCRIPTION = "A cleaner with which to clean the data";
    public static final String CLEAN_FLAG_SHORT = "-cl";
    public static final String CLEAN_FLAG_LONG = "--cleaner";

    protected static final String SERIALIZATION_FORMAT_DESCRIPTION = "Format for serialized context files";
    protected static final String SERIALIZATION_FORMAT_FLAG_SHORT = "-f";
    protected static final String SERIALIZATION_FORMAT_FLAG_LONG = "--format";

    public static final String PROCESS_DIRECTORY_DESCRIPTION = "A directory to be used by the process";
    public static final String PROCESS_DIRECTORY_FLAG_SHORT = "-p";
    public static final String PROCESS_DIRECTORY_FLAG_LONG = "--process-directory";

    protected static final List<String> DATA_SET_COLUMN_LABELS = Arrays.asList("id", "data", "code", "confidence");

    private static final Charset DEFAULT_CHARSET = LoadStep.DEFAULT_CHARSET_SUPPLIER.get();
    public static final String DEFAULT_DELIMITER = LoadStep.DEFAULT_DELIMITER;

    @Parameter(names = {NAME_FLAG_SHORT, NAME_FLAG_LONG}, description = NAME_DESCRIPTION)
    protected String name = PROCESS_NAME;

    @Parameter(names = {CHARSET_FLAG_SHORT, CHARSET_FLAG_LONG}, description = CHARSET_DESCRIPTION)
    protected List<CharsetSupplier> charsets;

    @Parameter(names = {DELIMITER_FLAG_SHORT, DELIMITER_FLAG_LONG}, description = DELIMITER_DESCRIPTION)
    protected List<String> delimiters;

    @Parameter(names = {SERIALIZATION_FORMAT_FLAG_SHORT, SERIALIZATION_FORMAT_FLAG_LONG}, description = SERIALIZATION_FORMAT_DESCRIPTION)
    protected SerializationFormat serialization_format = SerializationFormat.JAVA_SERIALIZATION;

    @Parameter(names = {PROCESS_DIRECTORY_FLAG_SHORT, PROCESS_DIRECTORY_FLAG_LONG}, description = PROCESS_DIRECTORY_DESCRIPTION, converter = PathConverter.class)
    protected Path process_directory;

    @Override
    public Void call() throws Exception {

        output("loading context...");

        final ClassificationContext context = Serialization.loadContext(process_directory, name, serialization_format);

        output("done");

        perform(context);

        output("saving context...");

        Serialization.persistContext(context, process_directory, name, serialization_format);

        output("done");

        return null;
    }

    protected static void output(String message) {

        Logging.output(InfoLevel.VERBOSE, message);
    }

    protected static String[] makeCleaningArgs(String command_name, List<CleanerSupplier> cleaners) {

        String[] args = {command_name};

        for (CleanerSupplier cleaner : cleaners) {
            args = extendArgs(args, CLEAN_FLAG_SHORT, cleaner.name());
        }
        return args;
    }

    protected static String[] addArgs(SerializationFormat serialization_format, String process_name, Path process_directory, String... args) {

        String[] result = extendArgs(args, SERIALIZATION_FORMAT_FLAG_SHORT, serialization_format.name(), NAME_FLAG_SHORT, process_name);

        if (process_directory != null) {
            result = extendArgs(result, InitCommand.PROCESS_DIRECTORY_FLAG_SHORT, process_directory.toString());
        }

        return result;
    }

    protected static String[] extendArgs(String[] args, String... additional_args) {

        String[] result = Arrays.copyOf(args, args.length + additional_args.length);

        System.arraycopy(additional_args, 0, result, args.length, additional_args.length);

        return result;
    }

    protected static Charset getCharset(List<CharsetSupplier> charset_suppliers, int i) {

        return (charset_suppliers == null || charset_suppliers.size() <= i || charset_suppliers.get(i) == null) ?
                DEFAULT_CHARSET :
                charset_suppliers.get(i).get();
    }

    protected static Charset getLastCharset(List<CharsetSupplier> charset_suppliers) {

        return getLastCharsetSupplier(charset_suppliers).get();
    }

    protected static CharsetSupplier getLastCharsetSupplier(List<CharsetSupplier> charset_suppliers) {

        if (charset_suppliers == null || charset_suppliers.size() == 0) return LoadStep.DEFAULT_CHARSET_SUPPLIER;

        final CharsetSupplier last = charset_suppliers.get(charset_suppliers.size() - 1);
        return (last == null ? LoadStep.DEFAULT_CHARSET_SUPPLIER : last);
    }

    protected static String getDelimiter(List<String> delimiters, int i) {

        return (delimiters == null || i < 0 || i >= delimiters.size() || delimiters.get(i) == null) ? DEFAULT_DELIMITER : delimiters.get(i);
    }

    protected static String getLastDelimiter(List<String> delimiters) {

        return (delimiters == null) ? DEFAULT_DELIMITER : getDelimiter(delimiters, delimiters.size() - 1);
    }
}
