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
package old.record_classification_old.data_readers;

import org.junit.Ignore;
import org.junit.Test;
import old.record_classification_old.datastructures.bucket.Bucket;
import old.record_classification_old.datastructures.code.CodeDictionary;
import old.record_classification_old.datastructures.records.Record;
import old.record_classification_old.exceptions.InputFormatException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PilotDataFormatConverterTest {

    @Ignore
    @Test
    public void test() throws IOException, InputFormatException {

        File inputFile = new File(getClass().getResource("/pilotStudyTestCase.tsv").getFile());

        CodeDictionary cd = new CodeDictionary(inputFile);
        PilotDataFormatConverter converter = new PilotDataFormatConverter();

        Bucket bucket = new Bucket(converter.convert(inputFile, cd));

        Record record1 = bucket.getRecord(1);
        Record record2 = bucket.getRecord(2);
        Record record3 = bucket.getRecord(3);

        assertEquals("cardio vascular degeneration", record1.getDescription());
        assertEquals("rheumatoid arthritis pneumonia cardiac failure", record2.getDescription());
        assertEquals("senility; chronic bronchitis; myocarditis", record3.getDescription());
    }
}
