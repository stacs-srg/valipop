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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.util.tools.*;

import java.nio.file.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class ConfigurationTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

        if (Files.isDirectory(Configuration.CLI_HOME)) {
            FileManipulation.deleteDirectory(Configuration.CLI_HOME);
        }
    }

    @Test
    public void testLoad() throws Exception {

    }

    @Test
    public void testSerializationAndDeserialization() throws Exception {

        if (!Files.isDirectory(Configuration.CLI_HOME)) {
            Files.createDirectory(Configuration.CLI_HOME);
        }

        final Configuration expected = new Configuration();
        final Configuration.Unseen unseen = expected.newUnseen("test", false);
        final Bucket bucket = new Bucket();
        bucket.add(new Record(1, "test"));
        unseen.setBucket(bucket);
        expected.persist();

        final Configuration actual = Configuration.load();

        assertEquals(expected.getUnseens(), actual.getUnseens());

    }
}
