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
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.util.tools.Logging;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.CleanUnseenRecordsStep;

import java.io.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.*;

/**
 * Cleans the unseen data.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = CleanUnseenRecordsCommand.NAME, commandDescription = "Cleans loaded records")
public class CleanUnseenRecordsCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "clean";

    public static final String CLEAN_DESCRIPTION = "A cleaner with which to clean the data";
    public static final String CLEAN_FLAG_SHORT = "-cl";
    public static final String CLEAN_FLAG_LONG = "--cleaner";
    
    @Parameter(required = true, names = {CLEAN_FLAG_SHORT, CLEAN_FLAG_LONG}, description = CLEAN_DESCRIPTION, variableArity = true)
    private List<CleanerSupplier> cleaner_suppliers;

    public CleanUnseenRecordsCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();
        final List<? extends Configuration.Unseen> unseens = getRecords(configuration);
        List<Bucket> buckets = unseens.stream().map(unseen -> {
            try {
                return unseen.toBucket();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        for (final Supplier<Cleaner> supplier : cleaner_suppliers) {
            final Cleaner cleaner = supplier.get();
            buckets = cleaner.apply(buckets);
        }

        for (int i = 0; i < buckets.size(); i++) {

            final Bucket bucket = buckets.get(i);
            unseens.get(i).setBucket(bucket);
        }
    }

    protected List<? extends Configuration.Unseen> getRecords(final Configuration configuration) {

        return configuration.getUnseens();
    }
}
