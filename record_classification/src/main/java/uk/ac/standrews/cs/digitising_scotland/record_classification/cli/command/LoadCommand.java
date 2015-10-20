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
import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.nio.charset.*;
import java.nio.file.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * Command to load resources from a file.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = LoadCommand.NAME, commandDescription = "Load resource")
public class LoadCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "load";

    private static final Logger LOGGER = Logger.getLogger(LoadCommand.class.getName());

    @Parameter(names = "-charset", description = "The resource file's charset.")
    private CharsetSupplier charset_supplier = launcher.getConfiguration().getDefaultCharsetSupplier();

    @Parameter(required = true, names = "-from", description = "Path to the resource file", converter = PathConverter.class)
    private Path source;

    @Parameter(names = "-named", description = "The name by which this resource is referred to. If unspecified, the file name is used.")
    private String name;

    public LoadCommand(final Launcher launcher) {

        super(launcher);
    }

    public Charset getCharset() {

        return charset_supplier.get();
    }

    @Override
    public void run() {

        final JCommander commander = launcher.getCommander();
        final JCommander load_commander = commander.getCommands().get(NAME);

        final String command_name = load_commander.getParsedCommand();

        if (command_name == null) {
            throw new ParameterException("Please specify sub command");
        }

        final JCommander load_command_commander = load_commander.getCommands().get(command_name);
        final Command command = (Command) load_command_commander.getObjects().get(0);

        command.run();
    }

    public String getName() {

        return name == null ? getSource().getFileName().toString() : name;
    }

    public Path getSource() {

        return source;
    }
}
