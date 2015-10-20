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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.ClassifyUnseenRecordsStep;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.SaveDataStep;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.*;

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

    @Parameter(required = true, names = {DESTINATION_FLAG_SHORT, DESTINATION_FLAG_LONG}, description = DESTINATION_DESCRIPTION, converter = PathConverter.class)
    private Path destination;

    public ClassifyCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();
        final Classifier classifier = configuration.getClassifier();

        final List<Bucket> classified_unseen = configuration.getUnseens().stream().map(unseen -> {
            try {
                return unseen.toBucket();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).map(classifier::classify).collect(Collectors.toList());

        //TODO persist classified records
    }
}
