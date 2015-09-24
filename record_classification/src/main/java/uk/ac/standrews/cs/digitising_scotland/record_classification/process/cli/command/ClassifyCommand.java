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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.ClassifyUnseenRecordsStep;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.SaveDataStep;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.nio.file.Path;
import java.util.List;

/**
 * Classification command of the classification process.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = ClassifyCommand.NAME, commandDescription = "Classify unseen data")
public class ClassifyCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "classify";

    public static final String DESTINATION_DESCRIPTION = "Path to the place to persist the classified records.";
    public static final String DESTINATION_FLAG_SHORT = "-o";
    public static final String DESTINATION_FLAG_LONG = "--output";

    private static final String OUTPUT_DELIMITER = ",";

    @Parameter(required = true, names = {DESTINATION_FLAG_SHORT, DESTINATION_FLAG_LONG}, description = DESTINATION_DESCRIPTION, converter = PathConverter.class)
    private Path destination;

    public ClassifyCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        output("classifying data...");

        final ClassificationContext context = launcher.getContext();
        new ClassifyUnseenRecordsStep().perform(context);

        output("saving results...");

        final DataSet classified_data_set = context.getClassifiedUnseenRecords().toDataSet(DATA_SET_COLUMN_LABELS);
        new SaveDataStep(classified_data_set, destination).perform(context);
    }
}
