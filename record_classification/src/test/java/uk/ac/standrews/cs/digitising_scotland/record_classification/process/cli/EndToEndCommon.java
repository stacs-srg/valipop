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

import org.junit.Before;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifiers;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaners;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.composite.InitLoadCleanTrainCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.composite.LoadCleanClassifyCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class EndToEndCommon {

    static final String CLASSIFIED_FILE_NAME = "classified.csv";
    static final String GOLD_STANDARD_FILE_NAME = "test_training_data.csv";
    static final String EVALUATION_FILE_NAME = "test_evaluation_data.csv";

    SerializationFormat serialization_format;
    String process_name;
    Classifiers classifier_supplier;
    double training_ratio;
    List<Cleaners> cleaners;

    Path temp_process_directory;
    Path input_gold_standard_file;
    Path output_trained_model_file;

    Path input_unseen_data_file;
    Path output_classified_file;

    @Before
    public void setup() throws IOException {

        process_name = Command.PROCESS_NAME;
        training_ratio = 0.8;

        cleaners = Arrays.asList(Cleaners.STOP_WORDS, Cleaners.PORTER_STEM, Cleaners.CONSISTENT_CLASSIFICATION_CLEANER_CORRECT);

        temp_process_directory = Files.createTempDirectory(process_name + "_");
        output_trained_model_file = Serialization.getSerializedContextPath(temp_process_directory, process_name, serialization_format);

        output_classified_file = Serialization.getProcessWorkingDirectory(temp_process_directory, process_name).resolve(CLASSIFIED_FILE_NAME);
    }

    protected void initLoadTrain() throws Exception {

        InitLoadCleanTrainCommand.initLoadCleanTrain(classifier_supplier, input_gold_standard_file, training_ratio, serialization_format, process_name, temp_process_directory, cleaners);
    }

    protected Path loadCleanClassify() throws Exception {

        LoadCleanClassifyCommand.loadCleanClassify(input_unseen_data_file, output_classified_file, serialization_format, process_name, temp_process_directory, cleaners);

        return output_classified_file;
    }
}
