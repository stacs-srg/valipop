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
package old.record_classification_old.classifiers;

import old.record_classification_old.datastructures.Pair;
import old.record_classification_old.datastructures.bucket.Bucket;
import old.record_classification_old.datastructures.code.Code;
import old.record_classification_old.datastructures.code.CodeNotValidException;
import old.record_classification_old.datastructures.records.Record;
import old.record_classification_old.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenSet;
import old.record_classification_old.legacy.naivebayes.NaiveBayesClassifier;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Ignore
public class NaiveBayesClassifierTest {

    /** The bucket a. */
    private Bucket bucketA;

    /** The bucket b. */
    private Bucket bucketB;

    /** The list of records. */
    private List<Record> listOfRecords;

    private ClassifierTestingHelper helper = new ClassifierTestingHelper();

    @Before
    public void setUp() throws Exception, CodeNotValidException {

        bucketB = createTrainingBucket();
        File tempFiles = new File("temp/");
        if (tempFiles.exists()) {
            FileUtils.deleteDirectory(tempFiles);
        }
    }

    @AfterClass
    public static void tearDown() throws IOException {

        File tempFiles = new File("temp/");
        File labelIndex = new File("labelindex.csv");
        File naiveBayesModelPath = new File("naiveBayesModelPath/");
        if (tempFiles.exists()) {
            FileUtils.deleteDirectory(tempFiles);
            FileUtils.deleteDirectory(naiveBayesModelPath);
            FileUtils.deleteQuietly(labelIndex);
        }
    }

    @Test
    public void testTrain() throws Exception {

        train();
    }

    private NaiveBayesClassifier train() throws Exception {

        NaiveBayesClassifier nbc = new NaiveBayesClassifier();
        nbc.train(bucketB);
        return nbc;
    }

    /**
     * Creates a training bucket.
     *
     * @return the training bucket
     * @throws Exception the exception
     */
    private Bucket createTrainingBucket() throws Exception, CodeNotValidException {

        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        bucketB = new Bucket(listOfRecordsTraining);
        bucketB = helper.giveBucketTestingOccCodes(bucketB);
        return bucketB;
    }

    @Test
    public void testClassify() throws Exception {

        NaiveBayesClassifier nbc = train();
        bucketA = new Bucket();

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);
        bucketA.addCollectionOfRecords(listOfRecords);

        //        LevenShteinCleaner.cleanData(bucketA);
    }

    @Test
    public void testClassifyTokenSet() throws Exception {

        NaiveBayesClassifier nbc = train();
        bucketA = new Bucket();

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);
        bucketA.addCollectionOfRecords(listOfRecords);

        for (Record r : bucketA) {
            TokenSet tokenSet = new TokenSet(r.getOriginalData().getDescription());
            Pair<Code, Double> result = nbc.classify(new TokenSet(r.getOriginalData().getDescription()));
        }
    }
}
