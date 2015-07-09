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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.AddTrainingAndEvaluationRecordsByRatio;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.TrainClassifier;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.File;
import java.io.FileReader;

/**
 * The train command of classification process command line interface.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = TrainCommand.NAME, commandDescription = "Train classifier")
class TrainCommand extends Command {

    /** The name of this command */
    public static final String NAME = "train";
    private static final long serialVersionUID = 8026292848547343006L;

    @Parameter(required = true, names = {"-g", "--goldStandard"}, description = "Path to a CSV file containing the gold standard.", converter = FileConverter.class)
    private File gold_standard;

    @Parameter(required = true, names = {"-r", "--trainingRecordRatio"}, description = "The ratio of gold standard records to be used for training. The value must be between 0.0 to 1.0 (inclusive).")
    private Double training_ratio;

    @Parameter(names = {"-d", "--delimiter"}, description = DELIMITER_DESCRIPTION)
    private char delimiter = DEFAULT_DELIMITER;

    @Override
    public void perform(final ClassificationContext context) throws Exception {

        final Bucket gold_standard_records = new Bucket(new DataSet(new FileReader(gold_standard), getDataFormat(delimiter)));

        new AddTrainingAndEvaluationRecordsByRatio(gold_standard_records, training_ratio).perform(context);
        new TrainClassifier().perform(context);
    }
}
