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
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.CleanerSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.Launcher;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.CleanGoldStandardStep;

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

    /** The name of this command. */
    public static final String NAME = "clean_gold_standard";

    @Parameter(required = true, names = {CLEAN_FLAG_SHORT, CLEAN_FLAG_LONG}, description = CLEAN_DESCRIPTION, variableArity = true)
    private List<CleanerSupplier> cleaner_suppliers;

    public CleanGoldStandardCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final ClassificationContext context = launcher.getContext();
        for (final Supplier<Cleaner> supplier : cleaner_suppliers) {
            new CleanGoldStandardStep(supplier.get()).perform(context);
        }
    }
}
