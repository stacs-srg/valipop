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
import uk.ac.standrews.cs.util.dataset.*;

import java.io.*;
import java.util.*;

/**
 * Clean command of the classification process.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = CleanCommand.NAME, commandDescription = "Cleans data records", separators = "=")
class CleanCommand extends Command {

    /** The name of this command */
    public static final String NAME = "clean";
    private static final long serialVersionUID = -5151083040631916098L;

    @Parameter(required = true, names = {"-c", "--cleaner"}, description = "Name of the cleaner by which to clean the source data.")
    private Cleaners cleaner;

    @Parameter(required = true, names = {"-i", "--input"}, description = "Path to the file containing the three colum data tobe cleaned.", converter = FileConverter.class)
    private File source;

    @Parameter(required = true, names = {"-o", "--output"}, description = "Path to store the cleaned data.", converter = FileConverter.class)
    private File destination;

    @Parameter(names = {"-d", "--delimiter"}, description = "The delimiter character of three column source data.")
    private char delimiter = '|';

    @Override
    public void perform(final ClassificationContext context) throws Exception {

        final CSVFormat input_format = CSVFormat.newFormat(delimiter);
        final DataSet source_dataset = new DataSet(new FileReader(source), input_format);
        final List<String> input_column_labels = source_dataset.getColumnLabels();
        final Bucket source_data = new Bucket(source_dataset);

        final Bucket cleaned_data = cleaner.clean(source_data);
        final DataSet cleaned_dataset = cleaned_data.toDataSet(input_column_labels, input_format);
        persistDataSet(destination.toPath(), cleaned_dataset);
    }
}
