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
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ClassifierSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;

import java.nio.file.*;
import java.util.Random;

/**
 * Initialisation command of the classification process.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = InitCommand.NAME, commandDescription = "Initialise a new classification process")
public class InitCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "init";

    @Parameter(names = {"name"}, description = "The name of the classification process.")
    private String name;

    public InitCommand(final Launcher launcher) { super(launcher); }

    @Override
    public void run() {

        // TODO check if .classi already exists before initialisation; warn or override upon confirmation 

        if (Files.exists(Configuration.CLI_HOME)) {
            //TODO add force paramter to skip confirmation        
            //warn("classi is already initialised in this directory");
            //confirm("override existing? This ");
        }

        final Configuration configuration = new Configuration();
        configuration.setName(name);

        launcher.setConfiguration(configuration);
    }
}
