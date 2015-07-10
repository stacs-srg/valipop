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
import org.apache.commons.csv.CSVFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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


    @Override
    public void perform(final ClassificationContext context)  {

        try {
            final CSVFormat csv_format = getDataFormat(delimiter);
            final DataSet source_data_set = new DataSet(new FileReader(source), csv_format);

            final Bucket source_data = new Bucket(source_data_set);
            final Bucket cleaned_data = cleaner.apply(source_data);

            final DataSet cleaned_data_set = cleaned_data.toDataSet(source_data_set.getColumnLabels(), csv_format);

            persistDataSet(destination.toPath(), cleaned_data_set);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
