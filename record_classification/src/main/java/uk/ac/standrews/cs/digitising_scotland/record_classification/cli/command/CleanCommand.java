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
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.Configuration.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import static java.util.logging.Logger.getLogger;

/**
 * Cleans load gold standard and unseen records.
 *
 * @author Masih Hajiarab Derkani
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@Parameters(commandNames = CleanCommand.NAME, resourceBundle = Configuration.RESOURCE_BUNDLE_NAME, commandDescriptionKey = "command.clean.description")
public class CleanCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "clean";

    /** The short name of the command that specifies the cleaners by which to clean loaded gold standard and/or unseen records. **/
    public static final String OPTION_CLEANER_SHORT = "-c";

    /** The long name of the command that specifies the cleaners by which to clean loaded gold standard and/or unseen records. **/
    public static final String OPTION_CLEANER_LONG = "--cleaner";

    private static final Logger LOGGER = getLogger(CleanCommand.class.getName());

    @Parameter(required = true, names = {OPTION_CLEANER_SHORT, OPTION_CLEANER_LONG}, descriptionKey = "command.clean.cleaner.description", variableArity = true)
    private List<CleanerSupplier> cleaner_suppliers;

    /**
     * Instantiates the clean command for the given launcher.
     *
     * @param launcher the launcher to which this command belongs
     */
    public CleanCommand(final Launcher launcher) { super(launcher); }

    @Override
    public void run() {

        final Cleaner cleaner = getCombinedCleaner();
        final Configuration configuration = launcher.getConfiguration();

        //FIXME implement spelling correction and stop words cleaning using loaded lexicon.
        //TODO think about whether we need this: allow user to choose what to clean; i.e. gold standard all or by name, unseen all or by name.

        cleanGoldStandardRecords(cleaner, configuration);
        cleanUnseenRecords(cleaner, configuration);
    }

    private void cleanUnseenRecords(final Cleaner cleaner, final Configuration configuration) {

        LOGGER.info("cleaning unseen records...");
        clean(cleaner, configuration.getUnseens());
    }

    private void cleanGoldStandardRecords(final Cleaner cleaner, final Configuration configuration) {

        LOGGER.info("cleaning gold standard records...");
        clean(cleaner, configuration.getGoldStandards());
    }

    private void clean(final Cleaner cleaner, final List<? extends Unseen> unclean) {

        LOGGER.info(() -> "cleaning " + unclean.stream().map(Unseen::getName).reduce((one, another) -> one + ", " + another).orElse("skipped; no records are loaded to clean."));

        final List<Bucket> unclean_buckets = unclean.stream().map(Unseen::toBucket).collect(Collectors.toList());
        final List<Bucket> clean_buckets = cleaner.apply(unclean_buckets);

        for (int index = 0; index < unclean.size(); index++) {
            final Unseen resource = unclean.get(index);
            resource.setBucket(clean_buckets.get(index));
        }
    }

    private Cleaner getCombinedCleaner() {

        return cleaner_suppliers.stream().map(Supplier::get).reduce(Cleaner::andThen).orElseThrow(() -> new ParameterException("no cleaner is specified"));
    }
}
