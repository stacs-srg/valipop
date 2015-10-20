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
import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = LoadUnseenRecordsCommand.NAME, commandDescription = "Load unseen records")
public class LoadUnseenRecordsCommand extends LoadRecordsCommand {

    /** The name of this command. */
    public static final String NAME = "unseen";

    private static final Logger LOGGER = Logger.getLogger(LoadUnseenRecordsCommand.class.getName());

    public LoadUnseenRecordsCommand(LoadCommand load_command) {

        super(load_command);
    }

    @Override
    protected void updateConfiguration(final Stream<Record> records) {

        final Configuration configuration = launcher.getConfiguration();
        final Configuration.Unseen unseen = new Configuration.Unseen(load_command.getName());
        unseen.add(records);
        configuration.addUnseen(unseen);
    }

    @Override
    protected Record toRecord(final CSVRecord record) {

        final Integer id = getId(record);
        final String label = getLabel(record);

        return new Record(id, label);
    }
}
