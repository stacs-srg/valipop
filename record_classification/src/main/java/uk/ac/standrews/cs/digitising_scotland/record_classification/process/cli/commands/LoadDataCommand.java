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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Charsets;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.LoadDataStep;

import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = LoadDataCommand.NAME, commandDescription = "Train classifier")
public class LoadDataCommand extends Command {

    /**
     * The name of this command
     */
    public static final String NAME = "load_data";

    private static final long serialVersionUID = 8026292848547343006L;

    public static final String DATA_DESCRIPTION = "Path to a CSV file containing the data to be classified.";
    public static final String DATA_FLAG_SHORT = "-d";
    public static final String DATA_FLAG_LONG = "--data";

    @Parameter(required = true, names = {DATA_FLAG_SHORT, DATA_FLAG_LONG}, description = DATA_DESCRIPTION, converter = PathConverter.class)
    private Path data;

    @Override
    public void perform(final ClassificationContext context) {

        // If charsets are specified, use the last one for the unseen data file.
        Charset charset = getCharset();

        // If delimiters are specified, use the last one for the unseen data file.
        String delimiter = getDelimiter();

        System.out.println("loading data...");

        new LoadDataStep(data, charset, delimiter).perform(context);
    }

    private Charset getCharset() {

        return charsets != null && charsets.size() > 0 ? charsets.get(charsets.size() - 1).get() : LoadDataStep.DEFAULT_CHARSET.get();
    }

    private String getDelimiter() {

        return delimiters != null && delimiters.size() > 0 ? delimiters.get(delimiters.size() - 1) : LoadDataStep.DEFAULT_DELIMITER;
    }

    public static void loadData(Path unseen_data, Charsets charset, String delimiter, SerializationFormat serialization_format, String process_name, Path process_directory) throws Exception {

        Launcher.main(addArgs(
                makeDataArgs(unseen_data, charset, delimiter), serialization_format, process_name, process_directory));
    }

    private static String[] makeDataArgs(Path unseen_data, Charsets charset, String delimiter) {

        // Assume that 'charset' and 'delimiter' will both be null or both set.

        String[] args = new String[charset == null ? 3 : 7];

        args[0] = NAME;
        args[1] = DATA_FLAG_SHORT;
        args[2] = unseen_data.toString();

        if (charset != null) {

            args[3] = CHARSET_FLAG_SHORT;
            args[4] = charset.name();

            args[5] = DELIMITER_FLAG_SHORT;
            args[6] = delimiter;
        }

        return args;
    }
}
