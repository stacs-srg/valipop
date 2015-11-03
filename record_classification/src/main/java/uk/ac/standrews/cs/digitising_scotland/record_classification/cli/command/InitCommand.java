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
import org.apache.commons.io.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Initialisation command of the classification process.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = InitCommand.NAME, resourceBundle = Configuration.RESOURCE_BUNDLE_NAME, commandDescriptionKey = "command.init.description")
public class InitCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "init";

    /** The short option name which forces any existing configuration to be replaced upon initialisation. **/
    public static final String OPTION_FORCE_SHORT = "-f";

    /** The long option name which forces any existing configuration to be replaced upon initialisation. **/
    public static final String OPTION_FORCE_LONG = "--force";

    @Parameter(names = {OPTION_FORCE_SHORT, OPTION_FORCE_LONG}, descriptionKey = "command.init.force.description")
    private boolean replace_existing;

    public InitCommand(final Launcher launcher) { super(launcher, NAME); }

    public static class Builder extends Command.Builder {

        private boolean force;

        public Builder forcefully() {

            this.force = true;
            return this;
        }

        @Override
        public String[] build() {

            final List<String> arguments = new ArrayList<>();

            arguments.add(NAME);
            if (force) {
                arguments.add(OPTION_FORCE_SHORT);
            }

            return arguments.toArray(new String[arguments.size()]);
        }
    }

    @Override
    public void run() {

        //TODO working directory?

        try {
            checkDirectoryExistence(Configuration.CLI_HOME, replace_existing);
            assureDirectoryExists(Configuration.CLI_HOME);
        }
        catch (IOException e) {
            throw new RuntimeException("failed to construct configuration folder", e);
        }
    }

    static void checkDirectoryExistence(Path directory, boolean delete_if_exists) throws IOException {

        if (Files.isDirectory(Configuration.CLI_HOME)) {

            if (delete_if_exists) {
                FileUtils.deleteDirectory(Configuration.CLI_HOME.toFile());
            }
            else {
                throw new FileAlreadyExistsException("directory already exists: " + directory);
            }
        }
    }

    public static void assureDirectoryExists(final Path directory) throws IOException {

        if (!Files.isDirectory(directory)) {
            final Path directories = Files.createDirectories(directory);
            if (!Files.isDirectory(directories)) {
                throw new IOException("failed to create directory: " + directory);
            }
        }
    }
}
