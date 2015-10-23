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
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * Command to load a resource from the local file system.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = LoadCommand.NAME, resourceBundle = Configuration.RESOURCE_BUNDLE_NAME, commandDescriptionKey = "command.load.description")
public class LoadCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "load";

    /** The short name of the option that specifies the charset of the resource file to be load. **/
    public static final String OPTION_CHARSET_SHORT = "-c";

    /** The long name of the option that specifies the charset of the resource file to be load. **/
    public static final String OPTION_CHARSET_LONG = "--charset";

    /** The short name of the option that specifies the path to the resource file to be load. **/
    public static final String OPTION_SOURCE_SHORT = "-s";

    /** The long name of the option that specifies the path to the resource file to be load. **/
    public static final String OPTION_SOURCE_LONG = "--from";

    /** The short name of the option that specifies a name for the resource file to be load. **/
    public static final String OPTION_NAME_SHORT = "-n";

    /** The long name of the option that specifies a name for the resource file to be load. **/
    public static final String OPTION_NAME_LONG = "--named";

    /** The short name of the option that specifies whether to override an existing resource with the same name. **/
    public static final String OPTION_FORCE_SHORT = "-o";

    /** The long name of the option that specifies whether to override an existing resource with the same name. **/
    public static final String OPTION_FORCE_LONG = "--overrideExisting";

    private static final Logger LOGGER = Logger.getLogger(LoadCommand.class.getName());

    @Parameter(names = {OPTION_CHARSET_SHORT, OPTION_CHARSET_LONG}, descriptionKey = "command.load.charset.description")
    private CharsetSupplier charset_supplier = launcher.getConfiguration().getDefaultCharsetSupplier();

    @Parameter(required = true, names = {OPTION_SOURCE_SHORT, OPTION_SOURCE_LONG}, descriptionKey = "command.load.source.description", converter = PathConverter.class)
    private Path source;

    @Parameter(names = {OPTION_NAME_SHORT, OPTION_NAME_LONG}, descriptionKey = "command.load.name.description")
    private String name;

    @Parameter(names = {OPTION_FORCE_SHORT, OPTION_FORCE_LONG}, description = "command.load.force.description")
    private boolean override_existing = false;

    public LoadCommand(final Launcher launcher) { super(launcher); }

    @Override
    public void run() {

        final Command command = getCommand();
        LOGGER.fine(() -> "Detected sub command " + command);
        command.run();
    }

    protected Command getCommand() {

        //TODO move to a jcommander utility class; merge with duplicate functionality in launcher.
        final JCommander commander = launcher.getCommander();
        final JCommander load_commander = commander.getCommands().get(NAME);

        final String command_name = load_commander.getParsedCommand();
        validateCommandName(command_name);

        final JCommander load_command_commander = load_commander.getCommands().get(command_name);
        return (Command) load_command_commander.getObjects().get(0);
    }

    private void validateCommandName(final String command_name) {

        if (command_name == null) {
            throw new ParameterException("Please specify sub command");
        }
    }

    /**
     * Gets the charset of the resource to be loaded.
     * If no charset is specified via {@value #OPTION_CHARSET_SHORT} or {@value #OPTION_CHARSET_LONG} options, the
     * {@link Configuration#getDefaultCharsetSupplier() default charset} is used.
     *
     * @return the charset of the resource to be loaded
     */
    public Charset getCharset() {

        return charset_supplier.get();
    }

    /**
     * Gets the name that is associated to the resource to be loaded.
     * The name offers a human-friendly way to refer to a specific loaded resource.
     * If no name is specified via {@value #OPTION_NAME_SHORT} or {@value #OPTION_NAME_LONG} options, the resource file name is used.
     *
     * @return the name that is associated to the loaded resource
     */
    public String getName() {

        return name == null ? getSource().getFileName().toString() : name;
    }

    /**
     * Gets the path to resource to be loaded.
     * The path is specified via {@value #OPTION_SOURCE_SHORT} or {@value #OPTION_SOURCE_LONG} options.
     *
     * @return the path to resource to be loaded
     */
    public Path getSource() {

        return source;
    }

    /**
     * Whether to override an existing resource with the same name.
     * This option may be set via {@value #OPTION_FORCE_SHORT} or {@value #OPTION_FORCE_LONG} options.
     * By default this option is disabled.
     *
     * @return whether to override an existing resource with the same name
     */
    public boolean isOverrideExistingEnabled() {

        return override_existing;
    }
}
