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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.composite;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.CleanerSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.CharsetSupplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Command;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.ClassifyCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.CleanDataCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.CleanGoldStandardCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.commands.LoadDataCommand;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.Serialization;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.SerializationFormat;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Composite command that loads data, cleans and classifies.
 * <p/>
 * Example command line invocation:
 * <p/>
 * <code>
 * java uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher
 * load_clean_classify
 * -p trained_hisco_classifier
 * -d unseen_data.csv
 * -o classified_data.csv
 * -f JSON_COMPRESSED
 * -cl COMBINED
 * </code>
 * <p/>
 * Or via Maven:
 * <p/>
 * mvn exec:java -q -Dexec.cleanupDaemonThreads=false
 * -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.Launcher" -e
 * -Dexec.args="load_clean_classify -p trained_hisco_classifier -d unseen_data.csv -o classified_data.csv -f JSON_COMPRESSED -cl COMBINED"
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = LoadCleanClassifyCommand.NAME, commandDescription = "Initialise process, load training data, and train classifier")
public class LoadCleanClassifyCommand extends Command {

    /**
     * The name of this command
     */
    public static final String NAME = "load_clean_classify";
    private static final long serialVersionUID = 8026292848547343006L;

    @Parameter(required = true, names = {LoadDataCommand.DATA_FLAG_SHORT, LoadDataCommand.DATA_FLAG_LONG}, description = LoadDataCommand.DATA_DESCRIPTION, converter = PathConverter.class)
    private Path unseen_data;

    @Parameter(required = true, names = {CleanGoldStandardCommand.CLEAN_FLAG_SHORT, CleanGoldStandardCommand.CLEAN_FLAG_LONG}, description = CleanGoldStandardCommand.CLEAN_DESCRIPTION)
    private List<CleanerSupplier> cleaners;

    @Parameter(required = true, names = {ClassifyCommand.DESTINATION_FLAG_SHORT, ClassifyCommand.DESTINATION_FLAG_LONG}, description = ClassifyCommand.DESTINATION_DESCRIPTION, converter = PathConverter.class)
    private Path destination;

    @Override
    public Void call() throws Exception {

        // If charsets are specified, use the last one for the unseen data file.
        CharsetSupplier charset_supplier = getLastCharsetSupplier(charsets);

        // If delimiters are specified, use the last one for the unseen data file.
        String delimiter = getLastDelimiter(delimiters);

        loadCleanClassifyUsingAPI(unseen_data, charset_supplier, delimiter, destination, process_directory, name, serialization_format, cleaners);

        return null;
    }

    public static void loadCleanClassify(Path unseen_data, CharsetSupplier charset_supplier, String delimiter, Path destination, Path process_directory, String process_name, SerializationFormat serialization_format, List<CleanerSupplier> cleaner_suppliers, boolean use_cli) throws Exception {

        if (use_cli) {
            loadCleanClassifyUsingCLI(unseen_data, charset_supplier, delimiter, destination, process_directory, process_name, serialization_format, cleaner_suppliers);
        }
        else {
            loadCleanClassifyUsingAPI(unseen_data, charset_supplier, delimiter, destination, process_directory, process_name, serialization_format, cleaner_suppliers);
        }
    }

    private static void loadCleanClassifyUsingAPI(Path unseen_data, CharsetSupplier charset_supplier, String delimiter, Path destination, Path process_directory, String process_name, SerializationFormat serialization_format, List<CleanerSupplier> cleaner_suppliers) throws Exception {

        output("loading model...");

        ClassificationContext context = Serialization.loadContext(process_directory, process_name, serialization_format);

        LoadDataCommand.perform(context, Arrays.asList(charset_supplier), Arrays.asList(delimiter), unseen_data);

        CleanDataCommand.perform(context, cleaner_suppliers);

        ClassifyCommand.perform(context, destination);

        output("saving context...");

        Serialization.persistContext(context, process_directory, process_name, serialization_format);
    }

    private static void loadCleanClassifyUsingCLI(Path unseen_data, CharsetSupplier charset_supplier, String delimiter, Path destination, Path process_directory, String process_name, SerializationFormat serialization_format, List<CleanerSupplier> cleaner_suppliers) throws Exception {

        LoadDataCommand.perform(serialization_format, process_name, process_directory,unseen_data, charset_supplier, delimiter);

        CleanDataCommand.perform(serialization_format, process_name, process_directory, cleaner_suppliers);

        ClassifyCommand.perform(serialization_format, process_name, process_directory,unseen_data, destination );
    }

    @Override
    public void perform(ClassificationContext context) {
    }
}
