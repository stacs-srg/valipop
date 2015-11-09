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

import junit.framework.*;
import org.junit.*;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * @author Masih Hajiarab Derkani
 */
public class EvaluateCommandTest extends CommandTest {

    @Test(expected = RuntimeException.class)
    public void testEvaluationFailureWithNoGoldStandard() throws Exception {

        init();
        evaluate();
    }

    @Test(expected = RuntimeException.class)
    public void testEvaluationFailureWithNoEvaluationRecords() throws Exception {

        init();
        loadGoldStandards(TestDataSets.CASE_1_TRAINING, 1.0);
        evaluate();
    }

    @Test(expected = RuntimeException.class)
    public void testEvaluationWithOutput() throws Exception {

        init();
        loadGoldStandards(TestDataSets.CASE_1_TRAINING, 8.0);
        final Path output = temp.newFile().toPath();
        evaluate(output);

        assertEvaluationOutputIsCorrect(output);
    }

    private void assertEvaluationOutputIsCorrect(final Path output) throws IOException {

        assertRegularFile(output);

        final Bucket records = Configuration.loadBucket(output);
        assertConsistentClassification(records);

    }

    private void assertRegularFile(final Path output) {assertTrue(Files.isRegularFile(output));}

    private void assertConsistentClassification(final Bucket records) {assertTrue(new ConsistentCodingChecker().test(Collections.singletonList(records)));}
}
