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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression;

import old.record_classification_old.pipeline.PipelineUtils;
import old.record_classification_old.tools.configuration.MachineLearningConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;

import java.io.File;

public class TrainClassifyOneFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainClassifyOneFile.class);
    private static final double DEFAULT_TRAINING_RATIO = 0.8;
    private static final String usageHelp = "usage: $" + TrainClassifyOneFile.class.getSimpleName() + "    <goldStandardDataFile>  <propertiesFile>  <trainingRatio(optional)>    <output multiple classifications";


    public Bucket run(final String[] args) throws Exception {

//        File goldStandard = parseGoldStandFile(args);
//        parseProperties(args);
//        double trainingRatio = parseTrainingRatio(args);
//        boolean multipleClassifications = false;
//
//        File codeDictionaryFile = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
//        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);
//
//        BucketGenerator generator = new BucketGenerator(codeDictionary);
//        Bucket allInputRecords = generator.generateTrainingBucket(goldStandard);
//
//        Bucket[] trainingPrediction = randomlyAssignToTrainingAndPrediction(allInputRecords, trainingRatio);
//        Bucket trainingBucket = trainingPrediction[0];
//        Bucket predictionBucket = trainingPrediction[1];
//
//        OLRClassifier olrClassifier = new OLRClassifier();
//        olrClassifier.train(trainingBucket);
//
//        IPipeline machineLearningClassifier = new ClassifierPipeline(olrClassifier, trainingBucket, new LogLengthWeightedLossFunction(), false, true);
//
//        Bucket notMachineLearned = machineLearningClassifier.classify(predictionBucket);
//
//        Bucket allClassified = machineLearningClassifier.getSuccessfullyClassified();
//
//        return BucketUtils.getUnion(allClassified, notMachineLearned);

        return null;
    }

    private static File parseGoldStandFile(final String[] args) {

        File goldStandard = null;
        if (args.length > 5) {
            System.err.println(usageHelp);
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);

        }
        return goldStandard;
    }

    public void parseProperties(String[] args) {

        if (args.length > 5) {
            System.err.println(usageHelp);
        }
        else {
            File properties = new File(args[1]);
            if (properties.exists()) {
                MachineLearningConfiguration.loadProperties(properties);
            }
        }
    }

    private boolean parseMultipleClassifications(final String[] args) {

        if (args.length > 5) {
            System.err.println(usageHelp);
        }
        else {
            if (args[3].equals("1")) { return true; }
        }
        return false;

    }

    private static double parseTrainingRatio(final String[] args) {

        double trainingRatio = DEFAULT_TRAINING_RATIO;
        if (args.length > 1) {
            double userRatio = Double.valueOf(args[2]);
            if (userRatio > 0 && userRatio < 1) {
                trainingRatio = userRatio;
            }
            else {
                System.err.println("trainingRatio must be between 0 and 1. Exiting.");
                System.exit(1);
            }
        }
        return trainingRatio;
    }

    private Bucket[] randomlyAssignToTrainingAndPrediction(final Bucket bucket, final double trainingRatio) {

        Bucket[] buckets = initBuckets();

        for (Record record : bucket) {
            if (Math.random() < trainingRatio) {
                buckets[0].add(record);
            }
            else {
                buckets[1].add(record);
            }
        }
        return buckets;
    }

    private Bucket[] initBuckets() {

        Bucket[] buckets = new Bucket[2];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket();
        }
        return buckets;
    }

}
