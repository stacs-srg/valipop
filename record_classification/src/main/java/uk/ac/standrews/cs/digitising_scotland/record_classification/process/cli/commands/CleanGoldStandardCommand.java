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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaners;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.CleanGoldStandardStep;

import java.nio.file.Path;
import java.util.List;

/**
 * Cleans the gold standard data.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = CleanGoldStandardCommand.NAME, commandDescription = "Cleans gold standard records", separators = "=")
public class CleanGoldStandardCommand extends Command {

    /** The name of this command */
    public static final String NAME = "clean_gold_standard";
    private static final long serialVersionUID = -5151083040631916098L;

    @Parameter(required = true, names = {CLEAN_FLAG_SHORT, CLEAN_FLAG_LONG}, description = CLEAN_DESCRIPTION)
    private List<Cleaners> cleaners;

    @Override
    public void perform(final ClassificationContext context)  {

        for (Cleaner cleaner : cleaners) {
            new CleanGoldStandardStep(cleaner).perform(context);
        }
    }

    public static void cleanGoldStandard(SerializationFormat serialization_format, String process_name, Path process_directory, List<Cleaners> cleaners) throws Exception {

        Launcher.main(addArgs(
                makeCleaningArgs(cleaners), serialization_format, process_name, process_directory));
    }

     private static String[] makeCleaningArgs(List<Cleaners> cleaners) {

        String[] args = new String[cleaners.size() * 2 + 1];

        args[0] = NAME;
        int index = 1;
        for (Cleaners cleaner : cleaners){
            args[index++] = CLEAN_FLAG_SHORT;
            args[index++] = cleaner.name();
        }
        return args;
    }
}
