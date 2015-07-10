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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.SerializationUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationProcessWithContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.Step;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.LoadGoldStandardFromFileStep;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Captures the common functionality among the command-line interface commands.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
abstract class Command implements Callable<Void>, Step {

    private static final String SERIALIZED_CLASSIFICATION_PROCESS_NAME = "process." + Launcher.PROGRAM_NAME;
    private static final Path SERIALIZED_CLASSIFICATION_PROCESS_PATH = Paths.get(SERIALIZED_CLASSIFICATION_PROCESS_NAME);

    private static final long serialVersionUID = -2176702491500665712L;

    public static final String DEFAULT_PROCESS_NAME = "classification_process";

    @Parameter(names = {"-n", "--name"}, description = "The name of the classification process.")
    protected String name = DEFAULT_PROCESS_NAME;

    @Parameter(names = {"-ch", "--charset"}, description = LoadGoldStandardFromFileStep.CHARSET_DESCRIPTION)
    protected Charsets charset = LoadGoldStandardFromFileStep.DEFAULT_CHARSET;

    @Parameter(names = {"-d", "--delimiter"}, description = LoadGoldStandardFromFileStep.DELIMITER_DESCRIPTION)
    protected char delimiter = LoadGoldStandardFromFileStep.DEFAULT_DELIMITER;

    @Override
    public Void call() throws Exception {

        final ClassificationProcessWithContext process = loadClassificationProcess();
        perform(process.getContext());
        persistClassificationProcess(process);
        return null;
    }

    protected void persistClassificationProcess(ClassificationProcess process) throws IOException {

        persistClassificationProcess(process, getSerializedClassificationProcessPath());
    }

    protected CSVFormat getDataFormat(char delimiter) {

        return DataSet.DEFAULT_CSV_FORMAT.withDelimiter(delimiter);
    }

    private ClassificationProcessWithContext loadClassificationProcess() throws IOException {

        Path serialized_classification_process_path = getSerializedClassificationProcessPath();

        if (!Files.isRegularFile(serialized_classification_process_path)) {

            throw new IOException("No suitable classification process file found; expected a file named " + serialized_classification_process_path + " at the current working directory.");
        }

        final byte[] process_bytes = Files.readAllBytes(serialized_classification_process_path);
        return (ClassificationProcessWithContext) SerializationUtils.deserialize(process_bytes);
    }

    private void persistClassificationProcess(ClassificationProcess process, Path destination) throws IOException {

        final byte[] process_bytes = SerializationUtils.serialize(process);
        Files.write(destination, process_bytes);
    }

    protected void persistDataSet(Path destination, final DataSet dataset) throws IOException {

        try (final BufferedWriter out = Files.newBufferedWriter(destination, StandardCharsets.UTF_8)) {
            dataset.print(out);
        }
    }

    protected Path getSerializedClassificationProcessPath() {

        return Paths.get(name).resolve(SERIALIZED_CLASSIFICATION_PROCESS_PATH);
    }
}
