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
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;
import uk.ac.standrews.cs.util.tools.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Launches the command line interface for a classification process.
 *
 * @author Masih Hajiarab Derkani
 */
public class Launcher {

    /** Name of this executable. */
    public static final String PROGRAM_NAME = "classy";

    private static final Pattern COMMAND_LINE_ARGUMENT_PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    protected static final String DEFAULT_CLASSIFICATION_PROCESS_NAME = "classification_process";
    private JCommander commander;
    private ClassificationContext context;

    protected static final String SERIALIZATION_FORMAT_DESCRIPTION = "Format for serialized context files";
    protected static final String SERIALIZATION_FORMAT_FLAG_SHORT = "-f";
    protected static final String SERIALIZATION_FORMAT_FLAG_LONG = "--format";

    public static final String PROCESS_DIRECTORY_DESCRIPTION = "A directory to be used by the process";
    public static final String PROCESS_DIRECTORY_FLAG_SHORT = "-p";
    public static final String PROCESS_DIRECTORY_FLAG_LONG = "--process-directory";

    @Parameter(names = {"-h", "--help"}, description = "Shows usage.", help = true)
    private boolean help;

    @Parameter(names = {"-n", "--name"}, description = "The name of the classification process.", help = true)
    private String name = DEFAULT_CLASSIFICATION_PROCESS_NAME;

    @Parameter(names = {SERIALIZATION_FORMAT_FLAG_SHORT, SERIALIZATION_FORMAT_FLAG_LONG}, description = SERIALIZATION_FORMAT_DESCRIPTION)
    protected SerializationFormat serialization_format = SerializationFormat.JAVA_SERIALIZATION;

    @Parameter(names = {PROCESS_DIRECTORY_FLAG_SHORT, PROCESS_DIRECTORY_FLAG_LONG}, description = PROCESS_DIRECTORY_DESCRIPTION, converter = PathConverter.class)
    protected Path process_directory;

//    @Parameter(names = {"-i", "--interactive"}, description = "Interactive mode; allows multiple command execution.")
//    private boolean interactive;

    @Parameter(names = {"-c", "--commands"}, description = "Path to a text file containing the commands to be executed (one command per line).", converter = PathConverter.class)
    private Path commands;

    private Launcher() {

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
            launcher.reportError("expected context file '" + e.getFile() + "' not found.");
        }
        catch (Exception e) {
            e.printStackTrace();
            launcher.reportError(e.getMessage());
        }
    }

    private void parse(final String... args) throws ParameterException {

        initCommander();
        commander.parse(args);
    }

    private void initCommander() {

        commander = new JCommander(this);
        commander.setProgramName(PROGRAM_NAME);

        addCommand(new ClassifyCommand(this));
        addCommand(new CleanUnseenRecordsCommand(this));
        addCommand(new CleanGoldStandardCommand(this));
        addCommand(new EvaluateCommand(this));
        addCommand(new InitCommand(this));
        addCommand(new LoadUnseenRecordsCommand(this));
        addCommand(new LoadGoldStandardCommand(this));
        addCommand(new TrainCommand(this));
    }

    private void handle() throws Exception {

        loadContext();
        try {

            if (commands != null) {
                handleCommandsFile();
            }
            else {
                handleCommand();
            }
        }
        finally {
            persistContext();
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

        final Path context_path = Serialization.getSerializedContextPath(process_directory, name, serialization_format);

        if (Files.isRegularFile(context_path)) {

            output("loading context...");
            context = Serialization.loadContext(process_directory, name, serialization_format);
        }
        else {
            context = null;
        }

        output("done");
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

        output("saving context...");

        Files.createDirectories(process_directory.resolve(name));
        Serialization.persistContext(context, process_directory, name, serialization_format);

        output("done");
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

    protected static void output(String message) {

        Logging.output(InfoLevel.VERBOSE, message);
    }

    public ClassificationContext getContext() {

        return context;
    }

    public void setContext(final ClassificationContext context) {

        this.context = context;
    }
}
