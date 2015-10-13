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
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.*;

import java.nio.file.Path;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = LoadUnseenRecordsCommand.NAME, commandDescription = "Train classifier")
public class LoadUnseenRecordsCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "load_data";

    public static final String DATA_DESCRIPTION = "Path to a CSV file containing the data to be classified.";
    public static final String DATA_FLAG_SHORT = "-d";
    public static final String DATA_FLAG_LONG = "--data";

    @Parameter(required = true, names = {DATA_FLAG_SHORT, DATA_FLAG_LONG}, description = DATA_DESCRIPTION, converter = PathConverter.class)
    private Path data;

    public static final String CHARSET_DESCRIPTION = "The data file charset";
    public static final String CHARSET_FLAG_SHORT = "-ch";
    public static final String CHARSET_FLAG_LONG = "--charset";
    @Parameter(names = {CHARSET_FLAG_SHORT, CHARSET_FLAG_LONG}, description = CHARSET_DESCRIPTION)
    protected CharsetSupplier charset = CharsetSupplier.UTF_8;

    public static final String DELIMITER_DESCRIPTION = "The data file delimiter character";
    public static final String DELIMITER_FLAG_SHORT = "-dl";
    public static final String DELIMITER_FLAG_LONG = "--delimiter";
    @Parameter(names = {DELIMITER_FLAG_SHORT, DELIMITER_FLAG_LONG}, description = DELIMITER_DESCRIPTION)
    protected String delimiter = LoadStep.DEFAULT_DELIMITER;

    public LoadUnseenRecordsCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        output("loading unseen records...");
        final ClassificationContext context = launcher.getContext();
        new LoadUnseenRecordsStep(data, charset.get(), delimiter).perform(context);
    }

}
