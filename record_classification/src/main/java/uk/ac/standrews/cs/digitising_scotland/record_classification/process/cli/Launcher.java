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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.composite.InitLoadCleanTrainCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.composite.LoadCleanClassifyCommand;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

/**
 * Launches the command line interface for a classification process.
 *
 * @author Masih Hajiarab Derkani
 */
public class Launcher {

    /** Name of this executable. */
    public static final String PROGRAM_NAME = "classy";

    private final JCommander commander;

    @Parameter(names = {"-h", "--help"}, description = "Shows usage.", help = true)
    private boolean help;

    private Launcher() {

        commander = new JCommander(this);
        commander.setProgramName(PROGRAM_NAME);

        addCommand(new ClassifyCommand());
        addCommand(new CleanDataCommand());
        addCommand(new CleanGoldStandardCommand());
        addCommand(new EvaluateCommand());
        addCommand(new InitCommand());
        addCommand(new LoadDataCommand());
        addCommand(new LoadGoldStandardCommand());
        addCommand(new TrainCommand());

        addCommand(new InitLoadCleanTrainCommand());
        addCommand(new LoadCleanClassifyCommand());
    }

    void addCommand(Command command) {

        commander.addCommand(command);
    }

    public static void main(String[] args) throws Exception {

        final Launcher launcher = new Launcher();

        try {
            launcher.parse(args);
            launcher.handle();
        }
        catch (ParameterException e) {
            launcher.reportParameterError(e.getMessage());
        }
        catch (FileAlreadyExistsException e) {
            launcher.reportError("process directory '" + e.getFile() + "' already exists.");
        }
        catch (NoSuchFileException e) {
            e.printStackTrace();
            launcher.reportError("expected context file '" + e.getFile() + "' not found.");
        }
        catch (Exception e) {
            e.printStackTrace();
            launcher.reportError(e.getMessage());
        }
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
            reportParameterError(help ? "" : "Please specify a command");
        }
    }

    private void reportParameterError(final String error_message) {

        System.err.println(error_message);
        commander.usage();
        System.exit(-1);
    }

    private void reportError(final String error_message) {

        System.err.println("Process failed: " + error_message);
        System.exit(-1);
    }
}
