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
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.PathConverter;
import org.apache.commons.csv.CSVFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.ClassifyUnseenRecordsStep;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Classification command of the classification process.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = ClassifyCommand.NAME, commandDescription = "Classify unseen data")
class ClassifyCommand extends Command {

    /** The name of this command */
    public static final String NAME = "classify";

    private static final long serialVersionUID = -5931407069557436051L;

    @Parameter(required = true, description = "Path to the unseen data to classify,", converter = PathConverter.class)
    private List<Path> unseen_data;

    @Parameter(required = true, names = {"-o", "--output"}, description = "Path to the place to persist the classified records.", converter = FileConverter.class)
    private File destination;

    @Override
    public void perform(final ClassificationContext context) {

        try {
            final CSVFormat input_format = getDataFormat(delimiter);
            final DataSet unseen_data_set = new DataSet(FileManipulation.getInputStreamReader(unseen_data.get(0)), input_format);
            final List<String> labels = unseen_data_set.getColumnLabels();
            final Bucket unseen_data = new Bucket(unseen_data_set);
            new ClassifyUnseenRecordsStep(unseen_data).perform(context);

            final DataSet classified_data_set = context.getClassifiedUnseenRecords().toDataSet(labels, input_format);
            persistDataSet(destination.toPath(), classified_data_set);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
