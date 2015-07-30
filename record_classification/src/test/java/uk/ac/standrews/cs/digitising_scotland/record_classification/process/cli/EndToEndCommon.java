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

import org.junit.After;
import org.junit.Before;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ClassifierSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.CleanerSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.composite.InitLoadCleanTrainCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.composite.LoadCleanClassifyCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.config.Config;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class EndToEndCommon {

    private static final String CLASSIFIED_FILE_NAME = "classified.csv";

    List<CharsetSupplier> gold_standard_charset_suppliers;
    CharsetSupplier unseen_data_charset_supplier;
    List<String> gold_standard_delimiters;
    String unseen_data_delimiter;

    SerializationFormat serialization_format;
    String process_name;
    ClassifierSupplier classifier_supplier;
    double training_ratio;
    List<CleanerSupplier> cleaners;
    boolean use_cli;

    Path temp_process_directory;
    List<Path> input_gold_standard_files;
    Path output_trained_model_file;

    Path input_unseen_data_file;
    Path output_classified_file;

    @Before
    public void setup() throws IOException {

        process_name = Command.PROCESS_NAME;
        training_ratio = 1.0;

        cleaners = Arrays.asList(CleanerSupplier.COMBINED);

        temp_process_directory = Files.createTempDirectory(process_name + "_");
        output_trained_model_file = Serialization.getSerializedContextPath(temp_process_directory, process_name, serialization_format);

        output_classified_file = Serialization.getProcessWorkingDirectory(temp_process_directory, process_name).resolve(CLASSIFIED_FILE_NAME);
    }

    @After
    public void cleanUp() {

        if (Config.cleanUpFilesAfterTests()) {

            try {
                FileManipulation.deleteDirectory(temp_process_directory);

            } catch (IOException e) {
                fail("could not delete temp directory");
            }
        }
    }

    protected void initLoadTrain() throws Exception {

        InitLoadCleanTrainCommand.initLoadCleanTrain(classifier_supplier, input_gold_standard_files, gold_standard_charset_suppliers, gold_standard_delimiters, training_ratio, serialization_format, process_name, temp_process_directory, cleaners, use_cli);
    }

    protected Path loadCleanClassify() throws Exception {

        LoadCleanClassifyCommand.loadCleanClassify(input_unseen_data_file, unseen_data_charset_supplier, unseen_data_delimiter, output_classified_file, temp_process_directory, process_name, serialization_format, cleaners, use_cli);

        return output_classified_file;
    }
}
