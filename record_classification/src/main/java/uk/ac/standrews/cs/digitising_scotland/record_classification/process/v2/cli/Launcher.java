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
import org.apache.commons.lang3.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps.*;

import java.io.*;
import java.nio.file.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class Launcher {

    private static final String PROGRAM_NAME = "classy";

    @Parameter(names = {"-h", "--help"}, description = "Shows usage.", help = true)
    private boolean help;

    private final InitCommand init_command = new InitCommand();
    private final CleanCommand clean_command = new CleanCommand();
    private final TrainCommand train_command = new TrainCommand();
    private final EvaluateCommand evaluate_command = new EvaluateCommand();
    private final ClassifyCommand classify_command = new ClassifyCommand();
    private final JCommander commander;

    private Launcher() {

        commander = new JCommander(this);
        commander.setProgramName(PROGRAM_NAME);
        addCommand(init_command);
        addCommand(clean_command);
        addCommand(train_command);
        addCommand(evaluate_command);
        addCommand(classify_command);
    }

    void addCommand(Step command) {

        commander.addCommand(command);
    }

    public static void main(String[] args) throws Exception {

        final Launcher launcher = new Launcher();

        try {
            //            launcher.parse("clean -c AAA,BAAA".split(" "));
            launcher.parse(new String[]{"clean", "-c", "STOP_WORDS", "-i", "sss", "-o", "sssss"});
            //            launcher.parse(args);
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

        final String command = commander.getParsedCommand();

        validateCommand(command);

        final JCommander commander = this.commander.getCommands().get(command);
        final Step step = (Step) commander.getObjects().get(0);

        switch (command) {
            case InitCommand.NAME: {
                final ClassificationProcess process = new ClassificationProcess();
                final Path working_directory = Files.createDirectory(Paths.get(init_command.getClassificationProcessName()));
                final Context context = process.getContext();
                init_command.perform(context);
                persist(process, working_directory);
            }
            break;
            case CleanCommand.NAME: {
                final ClassificationProcess process = loadClassificationProcess();
                final Context context = process.getContext();
                clean_command.perform(context);
                persist(process);
            }
            break;
            case TrainCommand.NAME: {

                final ClassificationProcess process = loadClassificationProcess();
                final Context context = process.getContext();
                train_command.perform(context);
                persist(process);
            }
            break;
            case EvaluateCommand.NAME: {
                final ClassificationProcess process = loadClassificationProcess();
                final Context context = process.getContext();
                final File destination = evaluate_command.getDestination();
                new EvaluateClassifier().perform(context);
                //FIXME implement repetition; where to save the repetition results?
                //FIXME implement persistence of evaluation results                
                persist(process);
            }

            break;
            case ClassifyCommand.NAME: {
                final ClassificationProcess process = loadClassificationProcess();
                classify_command.perform(process.getContext());
                persist(process);
            }
            default:
                throw new IllegalStateException();
        }
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

    private static void persist(final ClassificationProcess process, final Path working_directory) throws IOException {

        Files.write(working_directory.resolve("process"), SerializationUtils.serialize(process), StandardOpenOption.CREATE);
    }

    private static void persist(final ClassificationProcess process) throws IOException {

        Files.write(Paths.get("process"), SerializationUtils.serialize(process), StandardOpenOption.CREATE);
    }

    private static ClassificationProcess loadClassificationProcess() throws IOException {

        final byte[] process_bytes = Files.readAllBytes(Paths.get("process"));
        return (ClassificationProcess) SerializationUtils.deserialize(process_bytes);
    }

    private static Bucket readGoldStandard(File gold_standard_file) {
        //FIXME implement bar separated id, string, classification
        return null;
    }

    private static Bucket readUnseenData(File unseen_data_file) {
        //FIXME implement bar separated id, string, classification
        return null;
    }
}
