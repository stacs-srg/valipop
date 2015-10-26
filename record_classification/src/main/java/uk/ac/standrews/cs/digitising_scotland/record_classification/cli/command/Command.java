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
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.util.tools.Logging;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.LoadStep;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Represents an operation exposed to the user via the {@link Launcher command-line interface}.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public abstract class Command implements Runnable {

    protected final Launcher launcher;
    private String name;

    /**
     * Instantiates this command for the given launcher.
     *
     * @param launcher the launcher to which this command belongs.
     */
    public Command(Launcher launcher, String name) {

        this.launcher = launcher;
        this.name = name;
    }

    public final String getCommandName() {

        return name;
    }

    protected Optional<Command> subCommand() {

        final JCommander commander = launcher.getCommander();
        final JCommander load_commander = commander.getCommands().get(name);

        final String command_name = load_commander.getParsedCommand();

        final Optional<Command> command;
        if (command_name != null) {
            final JCommander load_command_commander = load_commander.getCommands().get(command_name);
            command = Optional.of((Command) load_command_commander.getObjects().get(0));
        }
        else {
            command = Optional.empty();
        }

        return command;
    }
}
