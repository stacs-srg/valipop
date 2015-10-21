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
import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.logging.*;
import java.util.stream.*;

/**
 * Command to load gold standard data from a file.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = LoadGoldStandardRecordsCommand.NAME, commandDescription = "Load gold standard data")
public class LoadGoldStandardRecordsCommand extends LoadUnseenRecordsCommand {

    /** The name of this command. */
    public static final String NAME = "gold_standard";

    /** The short name of the option that specifies the index of the column that contains the classes associated to each row, starting from {@value 0}. **/
    public static final String OPTION_CLASS_COLUMN_INDEX_SHORT = "-ci";

    /** The long name of the option that specifies the index of the column that contains the classes associated to each row, starting from {@value 0}. **/
    public static final String OPTION_CLASS_COLUMN_INDEX_LONG = "--class_column_index";

    /** The default index of the column that contains the classes associated to each row. **/
    public static final int DEFALUT_CLASS_COLUMN_INDEX = 2;

    private static final Logger LOGGER = Logger.getLogger(LoadGoldStandardRecordsCommand.class.getName());

    @Parameter(names = {SetCommand.OPTION_TRAINING_RATIO_SHORT, SetCommand.OPTION_TRAINING_RATIO_LONG},
                    description = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).",
                    validateValueWith = Validators.BetweenZeroToOneInclusive.class)
    private Double training_ratio = launcher.getConfiguration().getDefaultTrainingRatio();

    @Parameter(names = {OPTION_CLASS_COLUMN_INDEX_SHORT, OPTION_CLASS_COLUMN_INDEX_LONG}, description = "The zero-based index of the column containing the class associated to each label.")
    private Integer class_column_index = DEFALUT_CLASS_COLUMN_INDEX;

    /**
     * Instantiates this command as a sub command of the given load command.
     *
     * @param load_command the load command to which this command belongs.
     */
    public LoadGoldStandardRecordsCommand(final LoadCommand load_command) { super(load_command); }

    @Override
    protected void process(final Stream<Record> records) {

        final Configuration configuration = launcher.getConfiguration();
        final Configuration.GoldStandard gold_standard = new Configuration.GoldStandard(load_command.getName(), training_ratio);
        gold_standard.add(records);
        configuration.addGoldStandard(gold_standard);
    }

    @Override
    protected Record toRecord(final CSVRecord record) {

        LOGGER.finest(() -> String.format("loading record number %d, at character position %d", record.getRecordNumber(), record.getCharacterPosition()));

        final Integer id = getId(record);
        final String label = getLabel(record);
        final String clazz = getClass(record);

        return new Record(id, label, new Classification(clazz, new TokenList(label), 0.0, null));
    }

    /**
     * Gets the ratio of the gold standard records to be used for training.
     * The ratio must be specified via {@value SetCommand#OPTION_TRAINING_RATIO_SHORT} or {@value SetCommand#OPTION_TRAINING_RATIO_LONG} options
     * as a numerical value within inclusive range of {@code 0.0} to {@code 1.0}.
     *
     * @return the ratio of the gold standard records to be used for training
     */
    public Double getTrainingRatio() {

        return training_ratio;
    }

    private String getClass(final CSVRecord record) {

        return record.get(class_column_index);
    }
}
