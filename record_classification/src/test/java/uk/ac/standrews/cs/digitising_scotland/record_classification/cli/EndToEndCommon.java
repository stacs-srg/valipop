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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import org.junit.After;
import org.junit.Before;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ClassifierSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.CleanerSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.config.Config;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.IOException;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.fail;

public class EndToEndCommon {

    private static final String CLASSIFIED_FILE_NAME = "classified.csv";
    public static final char SPACE = ' ';
    public static final char DOUBLE_QUOTE = '\"';
    public static final char NEW_LINE = '\n';

    List<CharsetSupplier> gold_standard_charset_suppliers;
    CharsetSupplier unseen_data_charset_supplier;
    List<String> gold_standard_delimiters;
    String unseen_data_delimiter;

    SerializationFormat serialization_format;
    String process_name;
    ClassifierSupplier classifier_supplier;
    List<CleanerSupplier> cleaners;
    double internal_training_ratio;
    boolean use_cli;
    boolean include_ensemble_detail;

    Path temp_process_directory;
    List<Path> input_gold_standard_files;
    List<Double> training_ratios;
    Path output_trained_model_file;

    Path input_unseen_data_file;
    Path output_classified_file;

    @Before
    public void setup() throws IOException {

        process_name = Launcher.DEFAULT_CLASSIFICATION_PROCESS_NAME;

        cleaners = Collections.singletonList(CleanerSupplier.COMBINED);
        internal_training_ratio = TrainCommand.DEFAULT_INTERNAL_TRAINING_RATIO;

        temp_process_directory = Files.createTempDirectory(process_name + "_");
        output_trained_model_file = Serialization.getSerializedContextPath(temp_process_directory, process_name, serialization_format);

        output_classified_file = Serialization.getProcessWorkingDirectory(temp_process_directory, process_name).resolve(CLASSIFIED_FILE_NAME);
    }

    @After
    public void cleanUp() {

        if (Config.cleanUpFilesAfterTests()) {

            try {
                FileManipulation.deleteDirectory(temp_process_directory);
            }
            catch (IOException e) {
                fail("could not delete temp directory");
            }
        }
    }

    protected void initLoadTrain() throws Exception {

        StringBuilder commands_builder = new StringBuilder();

        appendInitCommand(commands_builder);
        appendLoadGoldStandardCommand(commands_builder);
        appendCleanGoldStandardCommand(commands_builder);
        appendTrainCommand(commands_builder);

        final Path commands = getCommandsFile(commands_builder);
        Launcher.main(getLauncherMainArguments(commands));
    }

    private Path getCommandsFile(final StringBuilder commands_builder) throws IOException {

        final Path commands = Files.createTempFile("test", "commands");
        commands.toFile().deleteOnExit();
        Files.write(commands, commands_builder.toString().getBytes(StandardCharsets.UTF_8));
        return commands;
    }

    private String[] getLauncherMainArguments(final Path commands) {

        return new String[]{"-f", serialization_format.name(), "-n", process_name, "-p", String.format("\"%s\"", temp_process_directory.toString()), "-c", String.format("\"%s\"", commands.toString())};
    }

    private void appendTrainCommand(final StringBuilder commands_builder) {

        commands_builder.append(TrainCommand.NAME);
        appendSpace(commands_builder);
        commands_builder.append(TrainCommand.INTERNAL_TRAINING_RATIO_FLAG_SHORT);
        appendSpace(commands_builder);
        commands_builder.append(internal_training_ratio);
        appendNewLine(commands_builder);
    }

    private void appendCleanGoldStandardCommand(final StringBuilder commands_builder) {

        commands_builder.append(CleanGoldStandardCommand.NAME);

        for (CleanerSupplier cleaner : cleaners) {
            appendSpace(commands_builder);
            commands_builder.append(CleanGoldStandardCommand.CLEAN_FLAG_SHORT);
            appendSpace(commands_builder);
            commands_builder.append(cleaner.name());
        }
        appendNewLine(commands_builder);
    }

    private void appendSpace(final StringBuilder commands_builder) {commands_builder.append(SPACE);}

    private void appendInitCommand(final StringBuilder commands_builder) {

        commands_builder.append(InitCommand.NAME);
        appendSpace(commands_builder);
        commands_builder.append("classifier");
        appendSpace(commands_builder);
        commands_builder.append(classifier_supplier.name());
        appendNewLine(commands_builder);
    }

    private void appendLoadGoldStandardCommand(final StringBuilder commands_builder) {

        for (int i = 0; i < input_gold_standard_files.size(); i++) {

            commands_builder.append(LoadGoldStandardCommand.NAME);
            appendSpace(commands_builder);

            final Path gold_standard = input_gold_standard_files.get(i);
            commands_builder.append(LoadGoldStandardCommand.GOLD_STANDARD_FLAG_SHORT);
            appendSpace(commands_builder);
            appendQuoted(commands_builder, gold_standard);

            if (gold_standard_delimiters != null && gold_standard_delimiters.size() > i) {

                final String delimiter = gold_standard_delimiters.get(i);

                if (delimiter != null) {
                    appendSpace(commands_builder);
                    commands_builder.append(LoadGoldStandardCommand.DELIMITER_FLAG_SHORT);
                    appendSpace(commands_builder);
                    commands_builder.append(delimiter);
                }
            }

            if (gold_standard_charset_suppliers != null && gold_standard_charset_suppliers.size() > i) {
                final CharsetSupplier charset = gold_standard_charset_suppliers.get(i);
                if (charset != null) {
                    appendSpace(commands_builder);
                    commands_builder.append(LoadGoldStandardCommand.CHARSET_FLAG_SHORT);
                    appendSpace(commands_builder);
                    commands_builder.append(charset.name());
                }
            }
            if (training_ratios != null && training_ratios.size() > i) {

                final Double training_ratio = training_ratios.get(i);
                if (training_ratio != null) {
                    appendSpace(commands_builder);
                    commands_builder.append(LoadGoldStandardCommand.TRAINING_RATIO_FLAG_SHORT);
                    appendSpace(commands_builder);
                    commands_builder.append(training_ratio);
                }
            }

            appendNewLine(commands_builder);
        }
    }

    private StringBuilder appendQuoted(final StringBuilder commands_builder, final Object value) {

        return commands_builder.append(DOUBLE_QUOTE).append(value).append(DOUBLE_QUOTE);
    }

    private void appendNewLine(final StringBuilder commands_builder) {commands_builder.append(NEW_LINE);}

    protected Path loadCleanClassify() throws Exception {

        StringBuilder commands_builder = new StringBuilder();

        appendLoadUnseenRecordsCommand(commands_builder);
        appendCleanUnseenRecordsCommand(commands_builder);
        appendClassifyCommand(commands_builder);

        final Path commands = getCommandsFile(commands_builder);
        Launcher.main(getLauncherMainArguments(commands));

        return output_classified_file;
    }

    private void appendClassifyCommand(final StringBuilder commands_builder) {

        commands_builder.append(ClassifyCommand.NAME);
        appendSpace(commands_builder);
        commands_builder.append(ClassifyCommand.DESTINATION_FLAG_SHORT);
        appendSpace(commands_builder);
        commands_builder.append(output_classified_file);
        appendNewLine(commands_builder);
    }

    private void appendCleanUnseenRecordsCommand(final StringBuilder commands_builder) {

        commands_builder.append(CleanUnseenRecordsCommand.NAME);
        appendSpace(commands_builder);

        for (CleanerSupplier cleaner : cleaners) {

            commands_builder.append(CleanUnseenRecordsCommand.CLEAN_FLAG_SHORT);
            appendSpace(commands_builder);
            commands_builder.append(cleaner.name());
            appendSpace(commands_builder);
        }
        appendNewLine(commands_builder);
    }

    private void appendLoadUnseenRecordsCommand(final StringBuilder commands_builder) {

        commands_builder.append(LoadUnseenRecordsCommand.NAME);
        appendSpace(commands_builder);
        commands_builder.append(LoadUnseenRecordsCommand.DATA_FLAG_SHORT);
        appendSpace(commands_builder);
        appendQuoted(commands_builder, input_unseen_data_file);

        if (unseen_data_delimiter != null) {
            appendSpace(commands_builder);
            commands_builder.append(LoadUnseenRecordsCommand.DELIMITER_FLAG_SHORT);
            appendSpace(commands_builder);
            commands_builder.append(unseen_data_delimiter);
        }

        if (unseen_data_charset_supplier != null) {
            appendSpace(commands_builder);
            commands_builder.append(LoadUnseenRecordsCommand.CHARSET_FLAG_SHORT);
            appendSpace(commands_builder);
            commands_builder.append(unseen_data_charset_supplier.name());
        }
        appendNewLine(commands_builder);
    }
}
