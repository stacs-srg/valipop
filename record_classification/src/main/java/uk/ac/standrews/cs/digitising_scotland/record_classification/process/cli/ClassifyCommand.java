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

import com.beust.jcommander.*;
import com.beust.jcommander.converters.*;
import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.*;
import uk.ac.standrews.cs.util.dataset.*;

import java.io.*;
import java.util.*;

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

    @Parameter(required = true, description = "Path to the unseen data to classify,", converter = FileConverter.class)
    private List<File> unseen_data;

    @Parameter(required = true, names = {"-o", "--output"}, description = "Path to the place to persist the classified records.", converter = FileConverter.class)
    private File destination;

    @Parameter(names = {"-d", "--delimiter"}, description = DELIMITER_DESCRIPTION)
    private char delimiter = DEFAULT_DELIMITER;

    @Override
    public void perform(final ClassificationContext context) throws Exception {

        final CSVFormat input_format = getDataFormat(delimiter);
        final DataSet unseen_data_set = new DataSet(new FileReader(unseen_data.get(0)), input_format);
        final List<String> labels = unseen_data_set.getColumnLabels();
        final Bucket unseen_data = new Bucket(unseen_data_set);
        new ClassifyUnseenRecords(unseen_data).perform(context);

        final DataSet classified_data_set = context.getClassifiedUnseenRecords().toDataSet(labels, input_format);
        persistDataSet(destination.toPath(), classified_data_set);
    }
}
