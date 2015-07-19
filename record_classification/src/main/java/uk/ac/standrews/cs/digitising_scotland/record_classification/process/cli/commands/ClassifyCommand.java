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
import org.apache.commons.csv.CSVFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.ClassifyUnseenRecordsStep;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Classification command of the classification process.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = ClassifyCommand.NAME, commandDescription = "Classify unseen data")
public class ClassifyCommand extends Command {

    /** The name of this command */
    public static final String NAME = "classify";

    private static final long serialVersionUID = -5931407069557436051L;

    public static final String UNSEEN_DATA_DESCRIPTION = "Path to the unseen data to classify,";

    public static final String DESTINATION_DESCRIPTION = "Path to the place to persist the classified records.";
    public static final String DESTINATION_FLAG_SHORT = "-o";
    public static final String DESTINATION_FLAG_LONG = "--output";

    @Parameter(required = true, description = UNSEEN_DATA_DESCRIPTION, converter = PathConverter.class)
    private List<Path> unseen_data;    // A list because required by JCommander.

    @Parameter(required = true, names = {DESTINATION_FLAG_SHORT, DESTINATION_FLAG_LONG}, description = DESTINATION_DESCRIPTION, converter = PathConverter.class)
    private Path destination;

    @Override
    public void perform(final ClassificationContext context) {

        try {
            final CSVFormat input_format = getDataFormat(delimiter);
//            final DataSet unseen_data_set = new DataSet(FileManipulation.getInputStreamReader(unseen_data.get(0)), input_format);
//            final List<String> labels = unseen_data_set.getColumnLabels();
//            final Bucket unseen_data = new Bucket(unseen_data_set);
            new ClassifyUnseenRecordsStep().perform(context);

            // TODO split into separate steps for classifying and exporting results.

            final DataSet classified_data_set = context.getClassifiedUnseenRecords().toDataSet(Arrays.asList("id", "data", "code"), input_format);
            persistDataSet(destination, classified_data_set);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void classify(Path unseen_data, Path destination, SerializationFormat serialization_format, String process_name, Path process_directory) throws Exception {

        Launcher.main(addArgs(
                new String[]{NAME, unseen_data.toString(), DESTINATION_FLAG_SHORT, destination.toString()}, serialization_format, process_name, process_directory));
    }
}
