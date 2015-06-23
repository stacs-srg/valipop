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
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

import java.nio.file.*;

/**
 * Initialisation command of the classification process.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = InitCommand.NAME, commandDescription = "Initialise a new classification process", separators = "=")
class InitCommand extends Command {

    /** The name of this command */
    public static final String NAME = "init";
    private static final long serialVersionUID = 5738604903474935932L;

    @Parameter(required = true, names = {"-n", "--name"}, description = "The name of the classification process.")
    private String name;

    @Parameter(required = true, names = {"-c", "--classifier"}, description = "The classifier to use for classification process.")
    private Classifiers classifier;

    @Override
    public Void call() throws Exception {

        final ClassificationProcess process = new ClassificationProcess();
        final Context context = process.getContext();
        perform(context);

        final Path process_working_directory = Paths.get(name);
        Files.createDirectory(process_working_directory);
        persistClassificationProcess(process, process_working_directory.resolve(SERIALIZED_CLASSIFICATION_PROCESS_PATH));

        return null; // void task
    }

    @Override
    public void perform(final Context context) throws Exception {

        context.setClassifier(classifier);
    }
}
