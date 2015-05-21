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
package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import com.google.common.collect.HashMultimap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class ExactMatchPipelineTest {

    ExactMatchClassifier exactMatchClassifier;
    ExactMatchPipeline pipeline;
    CodeDictionary codeDictionary;

    @Before
    public void setUp() throws Exception, CodeNotValidException {

        File trainingFile = new File(getClass().getResource("/TrainingDataModernCODFormatTest.txt").getFile());
        Bucket trainingBucket = createTrainingBucket(trainingFile);
        exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.train(trainingBucket);
        pipeline = new ExactMatchPipeline(exactMatchClassifier);
    }

    private Bucket createTrainingBucket(final File trainingFile) throws IOException, InputFormatException, CodeNotValidException {

        String codeDictionaryPath = getClass().getResource("/modCodeDictionary.txt").getFile();
        File codeDictionaryFile = new File(codeDictionaryPath);
        codeDictionary = new CodeDictionary(codeDictionaryFile);
        BucketGenerator b = new BucketGenerator(codeDictionary);
        return b.generateTrainingBucket(trainingFile);
    }

    @Test
    public void testFirstDescriptionLookup() throws IOException, CodeNotValidException {

        Set<Classification> classifications = exactMatchClassifier.classify(new TokenSet("Massive Pulmonary Embolism"));
        for (Classification classification : classifications) {

            assertEquals(codeDictionary.getCode("I26"), classification.getCode());
        }
    }

    @Test
    public void testSecondDescriptionLookup() throws IOException, CodeNotValidException {

        Set<Classification> classifications = exactMatchClassifier.classify(new TokenSet("old age"));
        for (Classification classification : classifications) {

            assertEquals(codeDictionary.getCode("R54"), classification.getCode());
        }
    }

    @Test
    public void testNoLookupLookup() throws IOException, CodeNotValidException {

        assertEquals(null, exactMatchClassifier.classify(new TokenSet("foobar")));
    }

    @Test
    public void testPipelineFirstDescriptionLookup() throws IOException, CodeNotValidException {

        assertTrue(exactMatchClassifier.classify(new TokenSet("Massive Pulmonary Embolism")).iterator().next().getCode().equals(codeDictionary.getCode("I26")));
    }

    @Test
    public void testPipelineSecondDescriptionLookup() throws IOException, CodeNotValidException {

        assertTrue(exactMatchClassifier.classify(new TokenSet("old age")).iterator().next().getCode().equals(codeDictionary.getCode("R54")));
    }

    @Test
    public void testPipelineNoLookupLookup() throws IOException, CodeNotValidException {

        assertEquals(null, exactMatchClassifier.classify(new TokenSet("foobar")));
    }

    @Test
    public void addResultToRecordTest() throws InputFormatException, IOException, CodeNotValidException {

        Record record = buildRecord(0, "description");

        String description = "new description";

        Set<Classification> result = exactMatchClassifier.classify(new TokenSet("old age"));
        record.addClassificationsToDescription(description, result);
        final HashMultimap<String, Classification> listOfClassifications = record.getListOfClassifications();
        assertTrue(listOfClassifications.containsKey(description));
        assertTrue(listOfClassifications.get(description).iterator().next().getCode().equals(codeDictionary.getCode("R54")));

    }

    @Test
    public void addResultsToRecordTest() throws InputFormatException, IOException, CodeNotValidException {

        Record record = buildRecord(0, "decription");

        String description = "new description";
        Set<Classification> result1 = exactMatchClassifier.classify(new TokenSet("old age"));
        Set<Classification> result2 = exactMatchClassifier.classify(new TokenSet("Massive Pulmonary Embolism"));
        result1.add(result2.iterator().next());

        record.addClassificationsToDescription(description, result1);
        final HashMultimap<String, Classification> listOfClassifications = record.getListOfClassifications();
        assertTrue(listOfClassifications.containsKey(description));
        final Iterator<Classification> iterator = listOfClassifications.get(description).iterator();
        assertTrue(iterator.next().getCode().equals(codeDictionary.getCode("R54")));
        assertTrue(iterator.next().getCode().equals(codeDictionary.getCode("I26")));
    }

    @Test
    public void testClassifyBucket() throws InputFormatException, IOException, CodeNotValidException {

        List<Record> list = new ArrayList<>();
        list.add(buildRecord(0, "old age"));
        list.add(buildRecord(1, "Massive Pulmonary Embolism"));
        list.add(buildRecord(2, "foo"));
        Bucket bucket = new Bucket(list);
        Bucket notClassified = pipeline.classify(bucket);
        Bucket classified = pipeline.getSuccessfullyClassified();

        assertEquals(codeDictionary.getCode("R54"), classified.getRecord(0).getListOfClassifications().get("old age").iterator().next().getCode());
        assertEquals(codeDictionary.getCode("I26"), classified.getRecord(1).getListOfClassifications().get("Massive Pulmonary Embolism").iterator().next().getCode());
        Assert.assertNull(classified.getRecord(2));
        assertTrue(notClassified.size() == 1);

    }

    private Record buildRecord(int i, String description) throws InputFormatException {

        return new Record(i, new OriginalData(description, 2014, 1, "filename"));
    }
}
