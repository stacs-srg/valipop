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

import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

/**
 * Represents an operation exposed to the user via the {@link Launcher command-line interface}.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public abstract class Command implements Runnable {

    private final String name;
    protected final Launcher launcher;
    protected final Logger logger;

    private static final Pattern SPECIAL_CHARACTER = Pattern.compile("[^-_A-Za-z0-9]");

    protected static abstract class Builder {

        /**
         * Builds the command-line arguments for this command.
         *
         * @return the command-line arguments of this command.
         */
        public abstract String[] build();

        /** Runs this command with the built arguments. */
        public void run() {

            final String[] unescaped_arguments = build();
            final String[] escaped_arguments = escapeSpecialCharacters(unescaped_arguments);
            
            Launcher.main(escaped_arguments);
        }

        public void run(Launcher launcher) throws Exception {
            final String[] unescaped_arguments = build();
            final String[] escaped_arguments = escapeSpecialCharacters(unescaped_arguments);
            launcher.parse(escaped_arguments);
            launcher.handle();
        }
        

        private String[] escapeSpecialCharacters(final String[] arguments) {

            for (int i = 0; i < arguments.length; i++) {

                final String argument = arguments[i];
                if (hasSpecialCharacter(argument)) {
                    arguments[i] = quote(argument);
                }
            }

            return arguments;
        }

        public static boolean hasSpecialCharacter(final String argument) {return SPECIAL_CHARACTER.matcher(argument).find();}

        public static String quote(Object value) { return String.format("\"%s\"", String.valueOf(value)); }
    }

    /**
     * Instantiates this command for the given launcher and the name by which it is triggered.
     *
     * @param launcher the launcher to which this command belongs.
     * @param name the name by which this command is triggered via the command line interface
     */
    public Command(final Launcher launcher, final String name) {

        this.launcher = launcher;
        this.name = name;

        logger = CLILogManager.CLILogger.getLogger(getClass().getName());
    }

    /**
     * Gets the name by which this command is triggered via the command line interface.
     *
     * @return the name by which this command is triggered via the command line interface
     */
    public final String getCommandName() {

        return name;
    }

    /**
     * Adds a given command to this command as its sub-command.
     *
     * @param sub_command the sub command to add to this command
     * @throws IllegalStateException if this command is not added to a launcher
     */
    public void addSubCommand(Command sub_command) {

        final JCommander sub_commander = launcher.getCommander().getCommands().get(name);
        if (sub_commander != null) {
            sub_commander.addCommand(sub_command);
        }
        else {
            throw new IllegalStateException("command must be added to launcher before adding sub commands");
        }
    }

    protected Optional<Command> getSubCommand() {

        final JCommander sub_commander = getSubCommander();
        final String command_name = sub_commander.getParsedCommand();

        final Optional<Command> command;
        if (command_name != null) {
            final JCommander load_command_commander = sub_commander.getCommands().get(command_name);
            command = Optional.of((Command) load_command_commander.getObjects().get(0));
        }
        else {
            command = Optional.empty();
        }

        return command;
    }

    private JCommander getSubCommander() {

        final JCommander core_commander = launcher.getCommander();
        return core_commander.getCommands().get(name);
    }
}
