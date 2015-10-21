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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import com.beust.jcommander.*;
import com.beust.jcommander.converters.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.util.tools.*;
import uk.ac.standrews.cs.util.tools.Logging;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

/**
 * Launches the command line interface for a classification process.
 *
 * @author Masih Hajiarab Derkani
 */
public class Launcher {

    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());

    /** The name of the folder that contains the persisted state of this program. */
    public static final String DEFAULT_CLASSIFICATION_PROCESS_NAME = "." + Configuration.PROGRAM_NAME;

    private static final Pattern COMMAND_LINE_ARGUMENT_PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    private JCommander commander;
    private ClassificationContext context;
    private Configuration configuration;

    @Parameter(names = {"-h", "--help"}, description = "Shows usage.", help = true)
    private boolean help;

//    @Parameter(names = {"-i", "--interactive"}, description = "Interactive mode; allows multiple command execution.")
//    private boolean interactive;

    @Parameter(names = {"-c", "--commands"}, description = "Path to a text file containing the commands to be executed (one command per line).", converter = PathConverter.class)
    private Path commands;

    @Parameter(names = "working_directory", description = "Path to the working directory.", converter = PathConverter.class)
    private Path working_directory;

    public Launcher() {

        configuration = new Configuration();
    }

    public static void main(String[] args) throws Exception {

        final Launcher launcher = new Launcher();

        try {
            launcher.parse(args);
            launcher.handle();
        }
        catch (ParameterException e) {
            launcher.exitWithError(e.getMessage());
        }
        catch (FileAlreadyExistsException e) {
            launcher.exitWithError("process directory '" + e.getFile() + "' already exists.");
        }
        catch (NoSuchFileException e) {
            launcher.exitWithError("expected context file '" + e.getFile() + "' not found.");
        }
        catch (Exception e) {
            LOGGER.log(Level.FINE, "failure", e);
            e.printStackTrace();
            launcher.exitWithError(e.getMessage());
        }
    }

    protected static void output(String message) {

        Logging.output(InfoLevel.VERBOSE, message);
    }

    void addCommand(Command command) {

        commander.addCommand(command);
    }

    public void parse(final String... args) throws ParameterException {

        initCommander();
        commander.parse(args);
    }

    private void initCommander() {

        commander = new JCommander(this);
        commander.setProgramName(Configuration.PROGRAM_NAME);

        addCommand(new InitCommand(this));
        addCommand(new SetCommand(this));
        addCommand(new ClassifyCommand(this));
        addCommand(new CleanCommand(this));
        addCommand(new EvaluateCommand(this));

        final LoadCommand load_command = new LoadCommand(this);
        addCommand(load_command);

        final JCommander load_commander = commander.getCommands().get(LoadCommand.NAME);
        load_commander.addCommand(new LoadUnseenRecordsCommand(load_command));
        load_commander.addCommand(new LoadGoldStandardCommand(load_command));

        addCommand(new TrainCommand(this));
    }

    public void handle() throws Exception {

//        loadContext();
        try {

            if (commands != null) {
                handleCommandsFile();
            }
            else {
                handleCommand();
            }
        }
        finally {
//            persistContext();
        }
    }

    private void handleCommandsFile() throws Exception {

        final List<String> command_lines = Files.readAllLines(commands);

        for (String command_line : command_lines) {
            String[] args = toCommandLineArguments(command_line);
            parse(args);
            handleCommand();
        }
    }

    private void loadContext() throws IOException {

        if (Files.isDirectory(Configuration.CLI_HOME)) {
            configuration = Configuration.load();
        }
    }

    private String[] toCommandLineArguments(final String command_line) {

        final List<String> arguments = new ArrayList<>();
        final Matcher matcher = COMMAND_LINE_ARGUMENT_PATTERN.matcher(command_line);
        while (matcher.find()) {
            if (matcher.group(1) != null) { // Add double-quoted string without the quotes
                arguments.add(matcher.group(1));
            }
            else if (matcher.group(2) != null) { // Add single-quoted string without the quotes
                arguments.add(matcher.group(2));
            }
            else { // Add unquoted word
                arguments.add(matcher.group());
            }
        }

        return arguments.toArray(new String[arguments.size()]);
    }

    private void handleCommand() throws Exception {

        final String command_name = commander.getParsedCommand();

        validateCommand(command_name);

        final JCommander command_commander = commander.getCommands().get(command_name);
        final Command command = (Command) command_commander.getObjects().get(0);

        command.run();
    }

    private void persistContext() throws IOException {

        if (Files.isDirectory(Configuration.CLI_HOME)) {
            configuration.persist();
        }
    }

    private void validateCommand(final String command) {

        if (command == null) {
            exitWithError(help ? "" : "Please specify a command", true);
        }
    }

    private void exitWithError(final String message) {

        exitWithError(message, false);
    }

    private void exitWithError(final String message, boolean show_usage) {

        System.err.println(message);
        if (show_usage) {
            commander.usage();
        }
        System.exit(-1);
    }

    public Configuration getConfiguration() {

        return configuration;
    }

    public void setConfiguration(final Configuration configuration) {

        this.configuration = configuration;
    }

    public JCommander getCommander() {

        return commander;
    }
}
