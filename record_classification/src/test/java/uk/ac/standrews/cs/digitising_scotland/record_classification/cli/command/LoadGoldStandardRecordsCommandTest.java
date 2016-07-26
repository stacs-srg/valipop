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

import org.apache.commons.csv.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Masih Hajiarab Derkani
 */
@RunWith(Parameterized.class)
public class LoadGoldStandardRecordsCommandTest extends LoadRecordsCommandTest {


    public LoadGoldStandardRecordsCommandTest(Bucket records, CSVFormat format, int id_index, int label_index, int code_index) {
        super(records, format, id_index, label_index, code_index);
    }


    @Override
    protected List<Object> getArguments() {

        final List<Object> arguments = super.getArguments();
        arguments.add(LoadGoldStandardRecordsCommand.OPTION_CLASS_COLUMN_INDEX_SHORT);
        arguments.add(code_index);
        return arguments;
    }

    @Override
    protected String getSubCommandName() {

        return LoadGoldStandardRecordsCommand.NAME;
    }

    @Override
    protected Bucket getActualRecords() {

        return configuration.getGoldStandardRecords();
    }
}
