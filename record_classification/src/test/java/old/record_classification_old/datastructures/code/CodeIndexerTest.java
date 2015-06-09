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
package old.record_classification_old.datastructures.code;

import old.record_classification_old.data_readers.LongFormatConverter;
import old.record_classification_old.datastructures.bucket.Bucket;
import old.record_classification_old.datastructures.records.Record;
import old.record_classification_old.datastructures.vectors.CodeIndexer;
import old.record_classification_old.exceptions.InputFormatException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Ignore
public class CodeIndexerTest {

    private CodeIndexer index;
    private Bucket bucket;
    private CodeDictionary codeDictionary;

    @Before
    public void setUp() throws Exception, CodeNotValidException {

        codeDictionary = new CodeDictionary(new File(getClass().getResource("/CodeCheckerTest.txt").getFile()));
        index = new CodeIndexer();
        List<Record> listOfRecords = createRecords();
        bucket = new Bucket(listOfRecords);
    }

    private List<Record> createRecords() throws IOException, InputFormatException, CodeNotValidException {

        LongFormatConverter lfc = new LongFormatConverter();
        File inputFile = new File(getClass().getResource("/multipleCauseRecordsTest.csv").getFile());
        List<Record> listOfRecords = lfc.convert(inputFile, codeDictionary);
        return listOfRecords;
    }

    @Test
    public void numberOfOutputClassesTest() throws CodeNotValidException {

        index.addGoldStandardCodes(bucket);
        Assert.assertEquals(7, index.getNumberOfOutputClasses());
    }

}
