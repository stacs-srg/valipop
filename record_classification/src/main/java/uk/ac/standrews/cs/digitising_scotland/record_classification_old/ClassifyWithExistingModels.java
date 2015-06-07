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
package uk.ac.standrews.cs.digitising_scotland.record_classification_old;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.classifiers.olr.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.classifiers.resolver.LengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.pipeline.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.tools.configuration.MachineLearningConfiguration;

import java.io.File;
import java.io.IOException;

public final class ClassifyWithExistingModels {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifyWithExistingModels.class);

    public static void main(final String[] args) throws Exception {

        new ClassifyWithExistingModels().run(args);
    }

    public Bucket run(final String[] args) throws IOException, CodeNotValidException, ClassNotFoundException, InputFormatException {

        File predictionFile = parsePredictionFile(args);
        String modelLocation = parseModelLocation(args);
        boolean multipleClassifications = parseMultipleClassifications(args);

        File codeDictionaryFile = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);
        BucketGenerator generator = new BucketGenerator(codeDictionary);
        Bucket allInputRecords = generator.createPredictionBucket(predictionFile);

        final ExactMatchClassifier existingExactMatchClassifier = PipelineUtils.getExistingExactMatchClassifier(modelLocation);
        final OLRClassifier existingOLRModel = PipelineUtils.getExistingOLRModel(modelLocation);

        IPipeline exactMatchPipeline = new ExactMatchPipeline(existingExactMatchClassifier);
        IPipeline machineLearningClassifier = new ClassifierPipeline(existingOLRModel, allInputRecords, new LengthWeightedLossFunction(), multipleClassifications, true);

        Bucket notExactMatched = exactMatchPipeline.classify(allInputRecords);
        Bucket notMachineLearned = machineLearningClassifier.classify(notExactMatched);
        Bucket successfullyClassifiedMachineLearning = machineLearningClassifier.getSuccessfullyClassified();

        Bucket allClassifed = BucketUtils.getUnion(exactMatchPipeline.getSuccessfullyClassified(), successfullyClassifiedMachineLearning);
        Bucket allOutputRecords = BucketUtils.getUnion(allClassifed, notMachineLearned);

        return allOutputRecords;
    }

    private boolean parseMultipleClassifications(final String[] args) {

        if (args.length > 3) {
            System.err.println("usage: $" + ClassifyWithExistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>    <output multiple classifications");
        }
        else {
            if (args[2].equals(Boolean.TRUE.toString())) { return true; }
        }
        return false;
    }

    private File parsePredictionFile(final String[] args) {

        File goldStandard = null;
        if (args.length > 3) {
            System.err.println("usage: $" + ClassifyWithExistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);
        }
        return goldStandard;
    }

    private String parseModelLocation(final String[] args) {

        String modelLocation = null;
        if (args.length > 3) {
            System.err.println("usage: $" + ClassifyWithExistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            modelLocation = args[1];
            PipelineUtils.exitIfDoesNotExist(new File(modelLocation));
        }
        return modelLocation;
    }
}
