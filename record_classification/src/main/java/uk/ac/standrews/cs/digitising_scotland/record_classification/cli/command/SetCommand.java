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
import com.beust.jcommander.converters.*;
import org.apache.commons.beanutils.converters.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.nio.charset.*;
import java.nio.file.*;
import java.util.logging.*;

/**
 * Sets variables in the Command-line Interface {@link Configuration configuration}.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = SetCommand.NAME, commandDescription = "Sets a variable in the configuration of this program.")
public class SetCommand extends Command {

    private static final Logger LOGGER = Logger.getLogger(SetCommand.class.getName());

    /** The name of this command. */
    public static final String NAME = "set";

    /** The short name of the option that specifies the {@link ClassifierSupplier classifier}. */
    public static final String OPTION_CLASSIFIER_SHORT = "-c";

    /** The long name of the option that specifies the {@link ClassifierSupplier classifier}. */
    public static final String OPTION_CLASSIFIER_LONG = "--classifier";

    /** The short name of the option that specifies the seed of configuration's random number generator. **/
    public static final String OPTION_RANDOM_SEED_SHORT = "-r";

    /** The short name of the option that specifies the seed of configuration's random number generator. **/
    public static final String OPTION_RANDOM_SEED_LONG = "--randomSeed";

    /** The short name of the option that specifies the default {@link CharsetSupplier charset} . **/
    public static final String OPTION_CHARSET_SHORT = "-ch";

    /** The Long name of the option that specifies the default {@link CharsetSupplier charset} . **/
    public static final String OPTION_CHARSET_LONG = "--charset";

    /** The short name of the option that specifies the default delimiter in input/output tabular data files. **/
    public static final String OPTION_DELIMITER_SHORT = "-d";

    /** The long name of the option that specifies the default delimiter in input/output tabular data files. **/
    public static final String OPTION_DELIMITER_LONG = "--delimiter";

    /** The short name of the option that specifies the {@link SerializationFormat format} in which to persist the state of this program. **/
    public static final String OPTION_SERIALIZATION_FORMAT_SHORT = "-s";

    /** The long name of the option that specifies the {@link SerializationFormat format} in which to persist the state of this program. **/
    public static final String OPTION_SERIALIZATION_FORMAT_LONG = "--serializationFormat";

    /** The short name of the option that specifies the ratio of the gold standard records to be used for training the classifier. **/
    public static final String OPTION_TRAINING_RATIO_SHORT = "-t";

    /** The short name of the option that specifies the ratio of the gold standard records to be used for training the classifier. **/
    public static final String OPTION_TRAINING_RATIO_LONG = "--trainingRatio";

    /** The short name of the option that specifies the internal training ratio of the classifier. **/
    public static final String OPTION_INTERNAL_TRAINING_RATIO_SHORT = "-it";

    /** The long name of the option that specifies the internal training ratio of the classifier. **/
    public static final String OPTION_INTERNAL_TRAINING_RATIO_LONG = "--internalTrainingRecordRatio";

    @Parameter(names = {OPTION_CLASSIFIER_SHORT, OPTION_CLASSIFIER_LONG}, description = "The classifier to use for the classification process.")
    private ClassifierSupplier classifier_supplier;

    @Parameter(names = {OPTION_RANDOM_SEED_SHORT, OPTION_RANDOM_SEED_LONG}, description = "The seed of random number generator.")
    private Long seed;

    @Parameter(names = {OPTION_CHARSET_SHORT, OPTION_CHARSET_LONG}, description = "The default charset of input/output files.")
    private CharsetSupplier charset_supplier;

    @Parameter(names = {OPTION_DELIMITER_SHORT, OPTION_DELIMITER_LONG}, description = "The default delimiter of input/output files.", converter = Converters.CharacterConverter.class)
    private Character delimiter;

    @Parameter(names = {OPTION_SERIALIZATION_FORMAT_SHORT, OPTION_SERIALIZATION_FORMAT_LONG}, description = "The format of serialised internal classification process settings.")
    private SerializationFormat serialization_format;

    @Parameter(names = {OPTION_TRAINING_RATIO_SHORT, OPTION_TRAINING_RATIO_LONG}, description = "The default internal training ratio.")
    private Double training_ratio;

    @Parameter(names = {OPTION_INTERNAL_TRAINING_RATIO_SHORT, OPTION_INTERNAL_TRAINING_RATIO_LONG}, description = "The default internal training ratio.")
    private Double internal_training_ratio;

    /**
     * Instantiates this command for the given launcher.
     *
     * @param launcher the launcher to which this format belongs.
     */
    public SetCommand(final Launcher launcher) { super(launcher); }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();

        boolean set = false;

        if (classifier_supplier != null) {
            LOGGER.info(() -> "Setting classifier to " + classifier_supplier);
            configuration.setClassifierSupplier(classifier_supplier);
            set = true;
        }

        if (seed != null) {
            LOGGER.info(() -> "Setting seed to " + seed);
            configuration.setSeed(seed);
            set = true;
        }

        if (charset_supplier != null) {
            LOGGER.info(() -> "Setting default charset to " + charset_supplier);
            configuration.setDefaultCharsetSupplier(charset_supplier);
            set = true;
        }

        if (delimiter != null) {
            LOGGER.info(() -> "Setting default delimiter to " + delimiter);
            configuration.setDefaultDelimiter(delimiter);
            set = true;
        }

        if (serialization_format != null) {
            LOGGER.info(() -> "Setting serialization format to " + serialization_format);
            configuration.setSerializationFormat(serialization_format);
            set = true;
        }

        if (training_ratio != null) {
            LOGGER.info(() -> "Setting default training ratio to " + training_ratio);
            configuration.setDefaultTrainingRatio(training_ratio);
            set = true;
        }
        if (internal_training_ratio != null) {
            LOGGER.info(() -> "Setting default internal training ratio to " + internal_training_ratio);
            configuration.setDefaultInternalTrainingRatio(internal_training_ratio);
            set = true;
        }

        if (!set) {
            throw new ParameterException("Please specify at lease one variable to be set.");
        }
    }
}
