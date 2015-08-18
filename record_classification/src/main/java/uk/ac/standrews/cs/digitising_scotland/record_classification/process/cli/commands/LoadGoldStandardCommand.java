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
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.LoadTrainingAndEvaluationRecordsByRatioStep;

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

    public static final String TRAINING_RATIO_DESCRIPTION = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).";
    public static final String TRAINING_RATIO_FLAG_SHORT = "-r";
    public static final String TRAINING_RATIO_FLAG_LONG = "--trainingRecordRatio";

    @Parameter(required = true, names = {TRAINING_RATIO_FLAG_SHORT, TRAINING_RATIO_FLAG_LONG}, description = TRAINING_RATIO_DESCRIPTION)
    private List<Double> training_ratios;

    @Override
    public void perform(final ClassificationContext context) {

        perform(context, gold_standards, training_ratios, charsets, delimiters);
    }

    public static void perform(final ClassificationContext context, List<Path> gold_standards, List<Double> training_ratios, List<CharsetSupplier> charsets, List<String> delimiters) {

        for (int i = 0; i < gold_standards.size(); i++) {

            Path gold_standard = gold_standards.get(i);
            double training_ratio = training_ratios.get(i);
            Charset charset = getCharset(charsets, i);
            String delimiter = getDelimiter(delimiters, i);

            new LoadTrainingAndEvaluationRecordsByRatioStep(gold_standard, training_ratio, charset, delimiter).perform(context);
        }
    }

    public static void perform(SerializationFormat serialization_format, String process_name, Path process_directory, List<Path> gold_standards, List<Double> training_ratios, List<CharsetSupplier> charsets, List<String> delimiters) throws Exception {

        Launcher.main(addArgs(serialization_format, process_name, process_directory, makeGoldStandardArgs(gold_standards, training_ratios, charsets, delimiters)));
    }

    private static String[] makeGoldStandardArgs(List<Path> gold_standards, List<Double> training_ratios, List<CharsetSupplier> charsets, List<String> delimiters) {

        String[] args = {NAME};

        for (int i = 0; i < gold_standards.size(); i++) {

            args = extendArgs(args, GOLD_STANDARD_FLAG_SHORT, gold_standards.get(i).toString());

            args = extendArgs(args, TRAINING_RATIO_FLAG_SHORT, String.valueOf(training_ratios.get(i).toString()));

            // Assume that 'charsets' and 'delimiters' will both be null or both set.
            if (charsets != null) {
                args = extendArgs(args, CHARSET_FLAG_SHORT, charsets.get(i).name(), DELIMITER_FLAG_SHORT, delimiters.get(i));
            }
        }

        return args;
    }
}
