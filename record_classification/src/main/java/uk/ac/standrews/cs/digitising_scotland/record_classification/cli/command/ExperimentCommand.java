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

import com.beust.jcommander.*;
import com.beust.jcommander.converters.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.nio.file.*;

/**
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = CleanCommand.NAME, resourceBundle = Configuration.RESOURCE_BUNDLE_NAME, commandDescriptionKey = "command.clean.description")
public class ExperimentCommand extends Command {

    /** The name of this command. **/
    public static final String NAME = "experiment";

    /** The default number of repetitions. **/
    public static final int DEFAULT_REPETITION_COUNT = 5;

    /** The short name of the option that specifies a name for the resource file to be load. **/
    public static final String OPTION_NAME_SHORT = "-n";

    /** The long name of the option that specifies a name for the resource file to be load. **/
    public static final String OPTION_NAME_LONG = "--named";

    @Parameter(names = {"-r", "--repeat"}, descriptionKey = "")
    private int repetition = DEFAULT_REPETITION_COUNT;

    @Parameter(names = {OPTION_NAME_SHORT, OPTION_NAME_LONG}, descriptionKey = "command.experiment.name.description")
    private String name;

    @Parameter(required = true, names = {Launcher.OPTION_COMMANDS_SHORT, Launcher.OPTION_COMMANDS_LONG}, descriptionKey = "launcher.commands.description", converter = PathConverter.class)
    private Path commands;

    /**
     * Instantiates this command for the given launcher and the name by which it is triggered.
     *
     * @param launcher the launcher to which this command belongs.
     */
    public ExperimentCommand(final Launcher launcher) {

        super(launcher, NAME);
    }

    @Override
    public void run() {

    }

}
