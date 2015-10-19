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

import java.io.*;
import java.nio.file.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * Command to load gold standard data from a file.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = LoadGoldStandardCommand.NAME, commandDescription = "Load gold standard data")
public class LoadGoldStandardCommand extends LoadUnseenRecordsCommand {

    /** The name of this command. */
    public static final String NAME = "load_gold_standard";
    public static final String TRAINING_RATIO_DESCRIPTION = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).";
    public static final String TRAINING_RATIO_FLAG = "trainingRatio";
    private static final Logger LOGGER = Logger.getLogger(LoadGoldStandardCommand.class.getName());
    @Parameter(required = true, names = TRAINING_RATIO_FLAG, description = TRAINING_RATIO_DESCRIPTION, validateValueWith = Validators.BetweenZeroAndOne.class)
    private Double training_ratio;

    @Parameter(names = "class_column_index", description = "The zero-based index of the column containing the class associated to each label.")
    private Integer class_column_index = 2;

    public LoadGoldStandardCommand(final Launcher launcher) {

        super(launcher);
    }

    protected void updateConfiguration(final Stream<Record> records) {

        final Configuration configuration = launcher.getConfiguration();
        final Configuration.GoldStandard gold_standard = new Configuration.GoldStandard(getSourceName(), training_ratio);
        gold_standard.add(records);
        configuration.addGoldStandard(gold_standard);
    }

    @Override
    protected Path getDataHome(final Configuration configuration) {

        return configuration.getGoldStandardHome();
    }

    @Override
    protected Record toRecord(final CSVRecord record) {

        final Integer id = getId(record);
        final String label = getLabel(record);
        final String clazz = getClass(record);

        return new Record(id, label, new Classification(clazz, new TokenList(label), 0.0, null));
    }

    private String getClass(final CSVRecord record) {

        return record.get(class_column_index);
    }
}
