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
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.CharsetSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.LoadGoldStandardStep;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

/**
 * Command to load gold standard data from one or more files.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = LoadGoldStandardCommand.NAME, commandDescription = "Load gold standard data")
public class LoadGoldStandardCommand extends Command {

    /**
     * The name of this command
     */
    public static final String NAME = "load_gold_standard";

    private static final long serialVersionUID = 8026292848547343006L;

    public static final String GOLD_STANDARD_DESCRIPTION = "Path to a CSV file containing the gold standard.";
    public static final String GOLD_STANDARD_FLAG_SHORT = "-g";
    public static final String GOLD_STANDARD_FLAG_LONG = "--goldStandard";

    @Parameter(required = true, names = {GOLD_STANDARD_FLAG_SHORT, GOLD_STANDARD_FLAG_LONG}, description = GOLD_STANDARD_DESCRIPTION, converter = PathConverter.class)
    private List<Path> gold_standards;

    @Override
    public void perform(final ClassificationContext context) {

        for (int i = 0; i < gold_standards.size(); i++) {

            Path gold_standard = gold_standards.get(i);
            Charset charset = getCharset(i);
            String delimiter = getDelimiter(i);

            new LoadGoldStandardStep(gold_standard, charset, delimiter).perform(context);
        }
    }

    private Charset getCharset(int i) {

        return charsets != null && charsets.size() > i ? charsets.get(i).get() : LoadGoldStandardStep.DEFAULT_CHARSET.get();
    }

    private String getDelimiter(int i) {

        return delimiters != null && delimiters.size() > i ? delimiters.get(i) : LoadGoldStandardStep.DEFAULT_DELIMITER;
    }

    public static void loadGoldStandard(List<Path> gold_standards, List<CharsetSupplier> charsets, List<String> delimiters, SerializationFormat serialization_format, String process_name, Path process_directory) throws Exception {

        Launcher.main(addArgs(
                makeGoldStandardArgs(gold_standards, charsets, delimiters), serialization_format, process_name, process_directory));
    }

    private static String[] makeGoldStandardArgs(List<Path> gold_standards, List<CharsetSupplier> charsets, List<String> delimiters) {

        // Assume that 'charsets' and 'delimiters' will both be null or both set.
        int number_of_args_per_gold_standard_file = charsets == null ? 2 : 6;

        String[] args = new String[gold_standards.size() * number_of_args_per_gold_standard_file + 1];

        args[0] = NAME;

        for (int i = 0; i < gold_standards.size(); i++) {

            args[i * number_of_args_per_gold_standard_file + 1] = GOLD_STANDARD_FLAG_SHORT;
            args[i * number_of_args_per_gold_standard_file + 2] = gold_standards.get(i).toString();

            if (charsets != null) {

                args[i * number_of_args_per_gold_standard_file + 3] = CHARSET_FLAG_SHORT;
                args[i * number_of_args_per_gold_standard_file + 4] = charsets.get(i).name();

                args[i * number_of_args_per_gold_standard_file + 5] = DELIMITER_FLAG_SHORT;
                args[i * number_of_args_per_gold_standard_file + 6] = delimiters.get(i);
            }
        }

        return args;
    }
}
