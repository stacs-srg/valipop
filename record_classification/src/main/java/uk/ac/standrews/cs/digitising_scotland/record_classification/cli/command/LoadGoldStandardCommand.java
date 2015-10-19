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

import java.io.*;
import java.nio.file.*;
import java.util.logging.*;

/**
 * Command to load gold standard data from a file.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = LoadGoldStandardCommand.NAME, commandDescription = "Load gold standard data")
public class LoadGoldStandardCommand extends LoadUnseenRecordsCommand {

    private static final Logger LOGGER = Logger.getLogger(LoadGoldStandardCommand.class.getName());

    /** The name of this command. */
    public static final String NAME = "load_gold_standard";

    public static final String TRAINING_RATIO_DESCRIPTION = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).";
    public static final String TRAINING_RATIO_FLAG = "trainingRatio";
    @Parameter(required = true, names = TRAINING_RATIO_FLAG, description = TRAINING_RATIO_DESCRIPTION, validateValueWith = Validators.BetweenZeroAndOne.class)
    private Double training_ratio;

    @Parameter(names = "class_column_index", description = "The index of the column containing the gold standard class associated to each label, starting from zero.")
    private Integer class_column_index = 1;

    public LoadGoldStandardCommand(final Launcher launcher) {

        super(launcher);
    }

    protected void loadRecord(final CSVPrinter printer, final CSVRecord record) {

        final String label = record.get(getLabelColumnIndex());
        final String clazz = record.get(class_column_index);

        try {
            printer.printRecord(label, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("failed to load gold standard record, no: %d, at: %d", record.getRecordNumber(), record.getCharacterPosition()), e);
        }
    }

    @Override
    protected Path getDataHome(final Configuration configuration) {

        return configuration.getGoldStandardHome();
    }

    protected void updateConfiguration() {

        final Configuration configuration = launcher.getConfiguration();
        final Configuration.GoldStandard gold_standard = new Configuration.GoldStandard();

        setFieldValues(configuration, gold_standard);
        gold_standard.setTrainingRatio(training_ratio);
        gold_standard.setClassColumnIndex(class_column_index);

        configuration.addGoldStandard(gold_standard);
    }
}
