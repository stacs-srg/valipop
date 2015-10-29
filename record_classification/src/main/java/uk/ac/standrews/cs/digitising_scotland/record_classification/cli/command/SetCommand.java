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
import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.util.function.*;
import java.util.logging.*;

import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.CAIParameters.*;

/**
 * Sets variables in the Command-line Interface {@link Configuration configuration}.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = SetCommand.NAME, resourceBundle = Configuration.RESOURCE_BUNDLE_NAME, commandDescriptionKey = "command.set.description")
public class SetCommand extends Command {

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

    /** The short name of the option that specifies the {@link CSVFormat format} of the input/output tabular data files. **/
    public static final String OPTIONS_FORMAT_SHORT = "-f";

    /** The long name of the option that specifies the {@link CSVFormat format} of the input/output tabular data files. **/
    public static final String OPTIONS_FORMAT_LONG = "--format";

    @Parameter(names = {ClassifierParameter.SHORT, ClassifierParameter.LONG}, descriptionKey = "command.set.classifier.description")
    private ClassifierSupplier classifier_supplier;

    @Parameter(names = {OPTION_RANDOM_SEED_SHORT, OPTION_RANDOM_SEED_LONG}, descriptionKey = "command.set.seed.description")
    private Long seed;

    @Parameter(names = {OPTION_CHARSET_SHORT, OPTION_CHARSET_LONG}, descriptionKey = "command.set.default_charset.description")
    private CharsetSupplier charset_supplier;

    @Parameter(names = {OPTION_DELIMITER_SHORT, OPTION_DELIMITER_LONG}, descriptionKey = "command.set.default_delimiter.description", converter = Converters.CharacterConverter.class)
    private Character delimiter;

    @Parameter(names = {OPTION_SERIALIZATION_FORMAT_SHORT, OPTION_SERIALIZATION_FORMAT_LONG}, descriptionKey = "command.set.serialization_format.description")
    private SerializationFormat serialization_format;

    @Parameter(names = {OPTION_TRAINING_RATIO_SHORT, OPTION_TRAINING_RATIO_LONG}, descriptionKey = "command.set.default_training_ratio.description")
    private Double training_ratio;

    @Parameter(names = {OPTION_INTERNAL_TRAINING_RATIO_SHORT, OPTION_INTERNAL_TRAINING_RATIO_LONG}, descriptionKey = "command.set.default_internal_training_ratio.description")
    private Double internal_training_ratio;

    @Parameter(names = {OPTIONS_FORMAT_SHORT, OPTIONS_FORMAT_LONG}, descriptionKey = "command.set.default_csv_format.description")
    private CsvFormatSupplier csv_format;

    /**
     * Instantiates this command for the given launcher.
     *
     * @param launcher the launcher to which this format belongs.
     */
    public SetCommand(final Launcher launcher) { super(launcher, NAME); }

    @Override
    public void run() {

        final Configuration configuration = launcher.getConfiguration();

        boolean set_at_least_once = set("classifier", classifier_supplier, configuration::setClassifierSupplier);
        set_at_least_once |= set("seed", seed, configuration::setSeed);
        set_at_least_once |= set("default charset", charset_supplier, configuration::setDefaultCharsetSupplier);
        set_at_least_once |= set("default delimiter", delimiter, configuration::setDefaultDelimiter);
        set_at_least_once |= set("classifier serialization format", serialization_format, configuration::setClassifierSerializationFormat);
        set_at_least_once |= set("default training ratio", training_ratio, configuration::setDefaultTrainingRatio);
        set_at_least_once |= set("default internal training ratio", internal_training_ratio, configuration::setDefaultInternalTrainingRatio);
        set_at_least_once |= set("default csv format", csv_format, configuration::setDefaultCsvFormatSupplier);

        if (!set_at_least_once) {
            throw new ParameterException("Please specify at lease one variable to be set.");
        }
    }

    private <Value> boolean set(String value_name, Value value, Consumer<Value> setter) {

        final boolean settable = value != null;
        if (settable) {
            setter.accept(value);
            logger.info(() -> String.format("The %s is set to %s", value_name, value));
        }
        return settable;
    }
}
