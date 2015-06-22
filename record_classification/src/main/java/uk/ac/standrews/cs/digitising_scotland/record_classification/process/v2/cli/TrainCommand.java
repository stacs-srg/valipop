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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.cli;

import com.beust.jcommander.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

import java.io.*;

/**
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandDescription = "Train classifier", separators = "=")
public class TrainCommand {

    @Parameter(required = true, names = {"-g", "--goldStandard"}, description = "Path to a CSV file containing the gold standard.", converter = CommandLineUtils.FileConverter.class)
    private File gold_standard;

    @Parameter(names = {"-c", "--cleanGoldStandard"}, description = "The name of the gold_standard_cleaner by which to clean the gold standard data prior to training/evaluation. May be one of: [NONE, CHECK, REMOVE, CORRECT]", converter = CommandLineUtils.CleanerConverter.class)
    private ConsistentCodingCleaner gold_standard_cleaner = ConsistentCodingCleaner.CORRECT;

    @Parameter(required = true, names = {"-r", "--trainingRecordRatio"}, description = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).")
    private Double training_ratio;

    public File getGoldStandard() {

        return gold_standard;
    }

    public ConsistentCodingCleaner getGoldStandardCleaner() {

        return gold_standard_cleaner;
    }

    public Double getTrainingRatio() {

        return training_ratio;
    }
}
