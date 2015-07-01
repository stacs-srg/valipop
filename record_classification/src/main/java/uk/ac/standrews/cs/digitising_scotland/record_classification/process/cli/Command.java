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

import org.apache.commons.lang3.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.util.dataset.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.concurrent.*;

/**
 * Captures the common functionality among the command-line interface commands.
 *
 * @author masih
 */
abstract class Command implements Callable<Void>, Step {

    static final String SERIALIZED_CLASSIFICATION_PROCESS_NAME = "process." + Launcher.PROGRAM_NAME;
    static final Path SERIALIZED_CLASSIFICATION_PROCESS_PATH = Paths.get(SERIALIZED_CLASSIFICATION_PROCESS_NAME);
    private static final long serialVersionUID = -2176702491500665712L;

    @Override
    public Void call() throws Exception {

        final ClassificationProcess process = loadClassificationProcess();
        perform(process.getContext());
        persistClassificationProcess(process);
        return null;
    }

    protected ClassificationProcess loadClassificationProcess() throws IOException {

        if (Files.isRegularFile(SERIALIZED_CLASSIFICATION_PROCESS_PATH)) {
            final byte[] process_bytes = Files.readAllBytes(SERIALIZED_CLASSIFICATION_PROCESS_PATH);
            return (ClassificationProcess) SerializationUtils.deserialize(process_bytes);
        }

        throw new IOException("No suitable classification process file found; expected a file named " + SERIALIZED_CLASSIFICATION_PROCESS_NAME + " at the current working directory.");
    }

    protected void persistClassificationProcess(ClassificationProcess process) throws IOException {

        persistClassificationProcess(process, SERIALIZED_CLASSIFICATION_PROCESS_PATH);
    }

    protected void persistClassificationProcess(ClassificationProcess process, Path destination) throws IOException {

        final byte[] process_bytes = SerializationUtils.serialize(process);
        Files.write(destination, process_bytes);
    }

    protected void persistDataSet(Path destination, final DataSet dataset) throws IOException {

        try (final BufferedWriter out = Files.newBufferedWriter(destination, Charset.defaultCharset())) {
            dataset.print(out);
        }
    }
}
