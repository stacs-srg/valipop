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

import org.apache.commons.io.*;
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class CommandTest {

    protected Launcher launcher;

    @Before
    public void setUp() throws Exception {

        deleteCliHome();
        launcher = new Launcher();
    }

    protected void run(Object... args) throws Exception {

        final List<String> arguments = Arrays.asList(args).stream().map(String::valueOf).collect(Collectors.toList());
        launcher.parse(arguments.toArray(new String[arguments.size()]));
        launcher.handle();
    }

    @After
    public void tearDown() throws Exception {

        deleteCliHome();
    }

    protected void deleteCliHome() throws IOException {FileUtils.deleteDirectory(Configuration.CLI_HOME.toFile());}
}
