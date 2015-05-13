/*
 * Copyright 2014 Digitising Scotland project:
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
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket;

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This Class, BucketTest, tests the construction of {@link Bucket} objects.
 */
public class BucketTest {

    /** The bucket a. */
    private Bucket bucketA = null;

    /** The list of records. */
    private List<Record> listOfRecords = null;

    /**
     * Set up. Populates listOfRecords.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);

    }

    /**
     * Test empty constructor.
     */
    @Test
    public void testEmptyConstructor() {

        bucketA = new Bucket();
        assertEquals(0, bucketA.size());
    }

    /**
     * Tests constructor.
     */
    @Test
    public void testConstructor() {

        bucketA = new Bucket(listOfRecords);

        assertEquals(listOfRecords.size(), bucketA.size());

        Iterator<Record> originalList = listOfRecords.iterator();

        for (Record record : bucketA) {
            assertEquals(originalList.next(), record);
        }
    }

    /**
     * Tests adding single record.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testAddSingleRecord() throws InputFormatException {

        bucketA = new Bucket();
        int id = (int) Math.rint(Math.random() * 1000); // TODO WTF?

        OriginalData originalData = new OriginalData("description", 1995, 1, "testFileName");
        Record recordToInsert = new Record(id, originalData);

        assertEquals(0, bucketA.size());

        bucketA.addRecordToBucket(recordToInsert);

        assertEquals(1, bucketA.size());

        assertEquals(recordToInsert, bucketA.iterator().next());
    }

    /**
     * Tests adding a list of records.
     */
    @Test
    public void testAddListOfRecords() {

        bucketA = new Bucket();
        bucketA.addCollectionOfRecords(listOfRecords);

        assertEquals(listOfRecords.size(), bucketA.size());

        Iterator<Record> originalList = listOfRecords.iterator();

        for (Record record : bucketA) {
            assertEquals(originalList.next(), record);
        }

    }

    /**
     * Tests getting a  record from it's UID.
     */
    @Test
    public void testGetRecordFromUID() {

        bucketA = new Bucket(listOfRecords);
        for (Record record : listOfRecords) {
            int uid = record.getid();
            assertEquals(record, bucketA.getRecord(uid));
        }

    }

}
