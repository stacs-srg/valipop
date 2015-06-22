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
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps.*;

import java.io.*;
import java.nio.file.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class Launcher {

    public static void main(String[] args) throws Exception {

        final InitCommand init_command = new InitCommand();
        final TrainCommand train_command = new TrainCommand();
        final EvaluateCommand evaluate_command = new EvaluateCommand();
        final ClassifyCommand classify_command = new ClassifyCommand();

        final Launcher launcher = new Launcher();

        final JCommander commander = new JCommander(launcher);
        commander.addCommand(init_command);
        commander.addCommand(train_command);
        commander.addCommand(evaluate_command);
        commander.addCommand(classify_command);

        try {
            commander.parse("init -n=aaaa -c=dummy".split(" "));
        }
        catch (ParameterException e) {
            System.err.println(e.getMessage());
            commander.usage();
            System.exit(1);
        }

        final String command = commander.getParsedCommand();

        switch (command) {
            case InitCommand.NAME: {
                final ClassificationProcess process = new ClassificationProcess();
                //TODO handle file already exists
                final Path working_directory = Files.createDirectory(Paths.get(init_command.getClassificationProcessName()));
                process.getContext().setClassifier(init_command.getClassifier());
                persist(process, working_directory);
            }
            break;
            case TrainCommand.NAME: {

                final ClassificationProcess process = loadClassificationProcess();
                final Bucket gold_standard = readGoldStandard(train_command.getGoldStandard());
                final ConsistentCodingCleaner cleaner = train_command.getGoldStandardCleaner();
                final Context context = process.getContext();
                context.setGoldStandard(cleaner.clean(gold_standard));
                new SetTrainingRecordsByRatio(train_command.getTrainingRatio()).perform(context);
                new TrainClassifier().perform(context);

                persist(process);
            }
            break;
            case EvaluateCommand.NAME: {
                final ClassificationProcess process = loadClassificationProcess();
                final File destination = evaluate_command.getDestination();
                final Context context = process.getContext();
                new EvaluateClassifier().perform(context);
                //FIXME implement repetition; where to save the repetition results?
                //FIXME implement persistence of evaluation results                
                persist(process);
            }

            break;
            case ClassifyCommand.NAME: {
                final ClassificationProcess process = loadClassificationProcess();
                final Bucket unseen_data = readUnseenData(classify_command.getUnseenData());
                final Context context = process.getContext();
                new ClassifyUnseenRecords(unseen_data).perform(context);

                //FIXME implement persistence of classification results                
                persist(process);
            }
            break;
            default:
                throw new IllegalStateException();
        }
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
