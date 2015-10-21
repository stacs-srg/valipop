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
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.serialization.*;

import java.util.*;
import java.util.function.*;

import static org.junit.Assert.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class SetCommandTest extends CommandTest {

    private static final Character[] TEST_DELIMITERS = new Character[]{',', '|', '?', '.', '<', '>', ':', ';'};
    private static final Long[] TEST_SEEDS = new Long[]{321L, 4L, 44444L, 514654L};

    @Test(expected = ParameterException.class)
    public void testUnspecifiedVariableFailure() throws Exception {

        run(SetCommand.NAME);
    }

    @Test
    public void testSetCharset() throws Exception {

        testSet(SetCommand.OPTION_CHARSET_SHORT, CharsetSupplier.values(), () -> launcher.getConfiguration().getDefaultCharsetSupplier());
        testSet(SetCommand.OPTION_CHARSET_LONG, CharsetSupplier.values(), () -> launcher.getConfiguration().getDefaultCharsetSupplier());
    }

    @Test
    public void testSetClassifier() throws Exception {

        testSet(SetCommand.OPTION_CLASSIFIER_SHORT, ClassifierSupplier.values(), () -> launcher.getConfiguration().getClassifierSupplier());
        testSet(SetCommand.OPTION_CLASSIFIER_LONG, ClassifierSupplier.values(), () -> launcher.getConfiguration().getClassifierSupplier());
    }

    @Test
    public void testSetDelimiter() throws Exception {

        testSet(SetCommand.OPTION_DELIMITER_SHORT, TEST_DELIMITERS, () -> launcher.getConfiguration().getDefaultDelimiter());
        testSet(SetCommand.OPTION_DELIMITER_LONG, TEST_DELIMITERS, () -> launcher.getConfiguration().getDefaultDelimiter());
    }

    @Test
    public void testSetRandomSeed() throws Exception {

        testSet(SetCommand.OPTION_RANDOM_SEED_SHORT, TEST_SEEDS, () -> launcher.getConfiguration().getRandom().nextLong(), value -> new Random(value).nextLong());
        testSet(SetCommand.OPTION_RANDOM_SEED_LONG, TEST_SEEDS, () -> launcher.getConfiguration().getRandom().nextLong(), value -> new Random(value).nextLong());
    }

    @Test
    public void testSetSerializationFormat() throws Exception {

        testSet(SetCommand.OPTION_SERIALIZATION_FORMAT_SHORT, SerializationFormat.values(), () -> launcher.getConfiguration().getSerializationFormat());
        testSet(SetCommand.OPTION_SERIALIZATION_FORMAT_LONG, SerializationFormat.values(), () -> launcher.getConfiguration().getSerializationFormat());
    }

    private <Value> void testSet(String option, Value[] values, Supplier<Value> actual) throws Exception {

        testSet(option, values, actual, value -> value);
    }

    private <Value> void testSet(String option, Value[] values, Supplier<Value> actual, Function<Value, Value> expected) throws Exception {

        for (Value value : values) {
            run(SetCommand.NAME, option, value);
            assertEquals(expected.apply(value), actual.get());
        }
    }
}
