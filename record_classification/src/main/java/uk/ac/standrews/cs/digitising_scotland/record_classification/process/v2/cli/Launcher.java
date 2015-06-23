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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.cli;

import com.beust.jcommander.*;

/**
 * Launches the command line interface for a classification process.
 *
 * @author Masih Hajiarab Derkani
 */
public class Launcher {

    /** Name of this executable. */
    public static final String PROGRAM_NAME = "classy";
    private static final String SERIALIZED_PROCESS_FILE_NAME = "process";
    private final InitCommand init_command = new InitCommand();
    private final CleanCommand clean_command = new CleanCommand();
    private final TrainCommand train_command = new TrainCommand();
    private final EvaluateCommand evaluate_command = new EvaluateCommand();
    private final ClassifyCommand classify_command = new ClassifyCommand();
    private final JCommander commander;

    @Parameter(names = {"-h", "--help"}, description = "Shows usage.", help = true)
    private boolean help;

    private Launcher() {

        commander = new JCommander(this);
        commander.setProgramName(PROGRAM_NAME);
        addCommand(init_command);
        addCommand(clean_command);
        addCommand(train_command);
        addCommand(evaluate_command);
        addCommand(classify_command);
    }

    void addCommand(Command command) {

        commander.addCommand(command);
    }

    public static void main(String[] args) throws Exception {

        final Launcher launcher = new Launcher();

        try {
            launcher.parse(args);
        }
        catch (ParameterException e) {
            e.printStackTrace();
            launcher.exitWithErrorMessage(e.getMessage());
        }

        launcher.handle();
    }

    private void parse(final String... args) throws ParameterException {

        commander.parse(args);
    }

    private void handle() throws Exception {

        final String command_name = commander.getParsedCommand();

        validateCommand(command_name);

        final JCommander commander = this.commander.getCommands().get(command_name);
        final Command command = (Command) commander.getObjects().get(0);

        command.call();
    }

    private void validateCommand(final String command) {

        if (command == null) {
            exitWithErrorMessage(help ? "" : "Please specify a command");
        }
    }

    private void exitWithErrorMessage(final String error_message) {

        System.err.println(error_message);
        commander.usage();
        System.exit(1);
    }

}
