/*
 * record_classification - Automatic record attribute classification.
 * Copyright © 2014-2016 Digitising Scotland project
 * (http://digitisingscotland.cs.st-andrews.ac.uk/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.apache.commons.io.*;
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.awt.*;
import java.nio.file.*;

import static org.junit.Assert.*;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command.InitCommand.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class InitCommandTest extends CommandTest {

    @Test
    public void testBasicInitialisation() throws Exception {

        initForcefully();

        assertTrue(Files.isDirectory(configuration.getHome()));
        assertTrue(Files.isRegularFile(configuration.getConfigurationFile()));
        assertTrue(Files.isDirectory(configuration.getInternalLogsHome()));
    }

    @Test(expected = RuntimeException.class)
    public void testBasicInitialisationFailureIfConfigurationExists() throws Exception {

        assureDirectoryExists(configuration.getHome());
        init();
    }

    @Test
    public void testForcedInitialisationIfConfigurationExists() throws Exception {

        assureDirectoryExists(configuration.getHome());
        initForcefully();
    }
}
