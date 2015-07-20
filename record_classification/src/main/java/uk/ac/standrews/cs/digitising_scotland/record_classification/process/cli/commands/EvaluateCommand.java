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
import com.beust.jcommander.converters.FileConverter;
import org.apache.commons.csv.CSVFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.EvaluateClassifierStep;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = EvaluateCommand.NAME, commandDescription = "Evaluate classifier")
public class EvaluateCommand extends Command {

    /**
     * The name of this command
     */
    public static final String NAME = "evaluate";
    private static final long serialVersionUID = 4285779171774505978L;

    private static final String OUTPUT_DELIMITER = ",";

    @Parameter(required = true, names = {"-o", "--output"}, description = "Path to the place to persist the evaluation results.", converter = FileConverter.class)
    private File destination;

    @Override
    public void perform(final ClassificationContext context) {

        // TODO split into multiple commands to classify and to output metrics.

        try {
            List<ClassificationMetrics> results = new ArrayList<>();

            new EvaluateClassifierStep().perform(context);
            results.add(context.getClassificationMetrics());

            final CSVFormat output_format = getDataFormat(OUTPUT_DELIMITER);
            final DataSet data_set = ClassificationMetrics.toDataSet(results, output_format);
            persistDataSet(destination.toPath(), data_set);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
