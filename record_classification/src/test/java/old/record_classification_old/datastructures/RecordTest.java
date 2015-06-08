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
package old.record_classification_old.datastructures;

import com.google.common.collect.HashMultimap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import old.record_classification_old.datastructures.classification.Classification;
import old.record_classification_old.datastructures.code.Code;
import old.record_classification_old.datastructures.code.CodeDictionary;
import old.record_classification_old.datastructures.code.CodeNotValidException;
import old.record_classification_old.datastructures.records.Record;
import old.record_classification_old.datastructures.tokens.TokenSet;
import old.record_classification_old.exceptions.InputFormatException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The Class RecordTest tests the creation of Records and their subclasses, parameters etc.
 * 
 */
//
public class RecordTest {

    /** The record. */
    private Record record;

    /** The original data. */
    private OriginalData originalData;

    @Before
    public void setUp() throws Exception {

        int id = (int) Math.rint(Math.random() * 1000);

        originalData = new OriginalData("A test Description", 2014, 1, "testFileName");
        record = new Record(id, originalData);
    }

    /**
     * Test constructor.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testConstructor() throws InputFormatException {

        assertEquals("A test Description", record.getOriginalData().getDescription());
    }

    /**
     * Test add classification set.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void testAddCodeTriples() throws IOException, CodeNotValidException {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeDictionary codeDictionary = new CodeDictionary(codeFile);
        Set<Classification> classificationSet = new HashSet<>();
        Code codeTest = codeDictionary.getCode("2100");

        Classification classification = new Classification(codeTest, new TokenSet("test String"), 1.0);
        classificationSet.add(classification);
        record.addClassification(classification.getTokenSet().toString(), classification);

        HashMultimap<String, Classification> classificationsFromRecord = record.getListOfClassifications();

         classification = classificationsFromRecord.entries().iterator().next().getValue();
        assertEquals("2100", classification.getCode().getCodeAsString());
        assertEquals("test string", classification.getTokenSet().toString());
    }

    /**
     * Test is cod method.
     *
     * @throws InputFormatException the input format exception
     */
    @Ignore
    @Test
    public void testIsCodMethod() throws InputFormatException {

        int id = (int) Math.rint(Math.random() * 1000);

        Record c = new Record(id, originalData);
        Assert.assertFalse(c.isCoDRecord());
        OriginalData codOriginalData = new CODOriginalData("A test Description", 2014, 1, 0, 0, "testFileName");
        c = new Record(id, codOriginalData);

        assertTrue(c.isCoDRecord());
    }

    /**
     * Test equals symmetric.
     */
    @Test
    public void testEqualsSymmetric() {

        int id = (int) Math.rint(Math.random() * 1000);
        Record x = new Record(id, originalData);
        Record y = new Record(id, originalData);
        assertTheSame(x, y);
    }

    /**
     * Test equals() different with classification sets where one is null.
     */
    @Test
    public void testEqualsSymmetricDifferentClassificationSetsNull() {

        int id = (int) Math.rint(Math.random() * 1000);
        Record x = new Record(id, originalData);
        Record y = new Record(id, originalData);
        Classification codeTriple = null;

        assertTheSame(x, y);
    }

    /**
     * Test equals symmetric different vectors.
     */
    @Test
    public void testEqualsSymmetricDifferentVectors() {

        int id = (int) Math.rint(Math.random() * 1000);

        Record x = new Record(id, originalData);
        Record y = new Record(id, originalData);
        assertTheSame(x, y);
    }

    /**
     * Assert different.
     *
     * @param x the x
     * @param y the y
     */
    private void assertDifferent(final Record x, final Record y) {

        assertTrue(!x.equals(y) && !y.equals(x));
        assertTrue(x.hashCode() != y.hashCode());
    }

    /**
     * Assert the same.
     *
     * @param x the x
     * @param y the y
     */
    private void assertTheSame(final Record x, final Record y) {

        assertTrue(x.equals(y) && y.equals(x));
        assertTrue(x.hashCode() == y.hashCode());
    }
}
