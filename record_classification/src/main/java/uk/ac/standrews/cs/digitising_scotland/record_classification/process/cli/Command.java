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
import org.apache.commons.csv.CSVFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.InitCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.Step;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    @Parameter(names = {NAME_FLAG_SHORT, NAME_FLAG_LONG}, description = NAME_DESCRIPTION)
    protected String name = PROCESS_NAME;

    @Parameter(names = {CHARSET_FLAG_SHORT, CHARSET_FLAG_LONG}, description = CHARSET_DESCRIPTION)
    protected List<Charsets> charsets;

    @Parameter(names = {DELIMITER_FLAG_SHORT, DELIMITER_FLAG_LONG}, description = DELIMITER_DESCRIPTION)
    protected List<String> delimiters;

    @Parameter(names = {SERIALIZATION_FORMAT_FLAG_SHORT, SERIALIZATION_FORMAT_FLAG_LONG}, description = SERIALIZATION_FORMAT_DESCRIPTION)
    protected SerializationFormat serialization_format = SerializationFormat.JAVA_SERIALIZATION;

    @Parameter(names = {PROCESS_DIRECTORY_FLAG_SHORT, PROCESS_DIRECTORY_FLAG_LONG}, description = PROCESS_DIRECTORY_DESCRIPTION, converter = PathConverter.class)
    protected Path process_directory;


    @Override
    public Void call() throws Exception {

        System.out.println("loading context...");
        final ClassificationContext context = loadContext();
        context.getClassifier().recoverFromDeserialization();
        System.out.println("done");

        perform(context);

        System.out.println("saving context...");
        context.getClassifier().prepareForSerialization();
        persistContext(context);
        System.out.println("done");

        return null;
    }

    private ClassificationContext loadContext() throws IOException {

        return Serialization.loadContext(getSerializedContextPath(), serialization_format);
    }

    protected void persistContext(ClassificationContext context) throws IOException {

        Serialization.persistContext(context, getSerializedContextPath(), serialization_format);
    }

    protected CSVFormat getDataFormat(String delimiter) {

        return DataSet.DEFAULT_CSV_FORMAT.withDelimiter(delimiter.charAt(0));
    }

    protected void persistDataSet(Path destination, final DataSet dataset) throws IOException {

        try (final BufferedWriter out = Files.newBufferedWriter(destination, StandardCharsets.UTF_8)) {
            dataset.print(out);
        }
    }

    private Path getSerializedContextPath() {

        return Serialization.getSerializedContextPath(process_directory, name, serialization_format);
    }

    protected static String[] addArgs(String[] args, SerializationFormat serialization_format, String process_name, Path process_directory) {

        int number_of_additional_args = process_directory == null ? 4 : 6;

        String[] result = Arrays.copyOf(args, args.length + number_of_additional_args);

        result[args.length] = SERIALIZATION_FORMAT_FLAG_SHORT;
        result[args.length + 1] = serialization_format.name();

        result[args.length + 2] = NAME_FLAG_SHORT;
        result[args.length + 3] = process_name;

        if (process_directory != null) {
            result[args.length + 4] = InitCommand.PROCESS_DIRECTORY_FLAG_SHORT;
            result[args.length + 5] = process_directory.toString();
        }
        return result;
    }
}
