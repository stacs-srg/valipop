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
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.CleanerSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.CleanGoldStandardStep;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

/**
 * Cleans the gold standard data.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = CleanGoldStandardCommand.NAME, commandDescription = "Cleans gold standard records", separators = "=")
public class CleanGoldStandardCommand extends Command {

    /**
     * The name of this command
     */
    public static final String NAME = "clean_gold_standard";
    private static final long serialVersionUID = -5151083040631916098L;

    @Parameter(required = true, names = {CLEAN_FLAG_SHORT, CLEAN_FLAG_LONG}, description = CLEAN_DESCRIPTION)
    private List<CleanerSupplier> cleaner_suppliers;

    @Override
    public void perform(final ClassificationContext context) {

        perform(context, cleaner_suppliers);
    }

    public static void perform(final ClassificationContext context, List<CleanerSupplier> cleaner_suppliers) {

        for (Supplier<Cleaner> supplier : cleaner_suppliers) {
            new CleanGoldStandardStep(supplier.get()).perform(context);
        }
    }

    public static void perform(SerializationFormat serialization_format, String process_name, Path process_directory, List<CleanerSupplier> cleaners) throws Exception {

        Launcher.main(addArgs(
                serialization_format, process_name, process_directory, makeCleaningArgs(NAME, cleaners)));
    }
}
