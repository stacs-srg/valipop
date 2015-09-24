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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.*;

import java.nio.file.Path;
import java.util.*;

/**
 * Command to load gold standard data from one or more files.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
@Parameters(commandNames = LoadGoldStandardCommand.NAME, commandDescription = "Load gold standard data")
public class LoadGoldStandardCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "load_gold_standard";

    public static final String GOLD_STANDARD_DESCRIPTION = "Path to a CSV file containing the gold standard.";
    public static final String GOLD_STANDARD_FLAG_SHORT = "-g";
    public static final String GOLD_STANDARD_FLAG_LONG = "--goldStandard";

    @Parameter(required = true, names = {GOLD_STANDARD_FLAG_SHORT, GOLD_STANDARD_FLAG_LONG}, description = GOLD_STANDARD_DESCRIPTION, converter = PathConverter.class)
    private Path gold_standard;

    public static final String TRAINING_RATIO_DESCRIPTION = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).";
    public static final String TRAINING_RATIO_FLAG_SHORT = "-r";
    public static final String TRAINING_RATIO_FLAG_LONG = "--trainingRecordRatio";

    @Parameter(required = true, names = {TRAINING_RATIO_FLAG_SHORT, TRAINING_RATIO_FLAG_LONG}, description = TRAINING_RATIO_DESCRIPTION, validateValueWith = Validators.BetweenZeroAndOne.class)
    private Double training_ratio;

    public static final String CHARSET_DESCRIPTION = "The data file charset";
    public static final String CHARSET_FLAG_SHORT = "-ch";
    public static final String CHARSET_FLAG_LONG = "--charset";
    @Parameter(names = {CHARSET_FLAG_SHORT, CHARSET_FLAG_LONG}, description = CHARSET_DESCRIPTION)
    protected CharsetSupplier charset = CharsetSupplier.UTF_8;

    public static final String DELIMITER_DESCRIPTION = "The data file delimiter character";
    public static final String DELIMITER_FLAG_SHORT = "-dl";
    public static final String DELIMITER_FLAG_LONG = "--delimiter";
    @Parameter(names = {DELIMITER_FLAG_SHORT, DELIMITER_FLAG_LONG}, description = DELIMITER_DESCRIPTION)
    protected String delimiter = LoadStep.DEFAULT_DELIMITER;

    public LoadGoldStandardCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        new LoadTrainingAndEvaluationRecordsByRatioStep(gold_standard, training_ratio, charset.get(), delimiter).perform(launcher.getContext());
    }
}
