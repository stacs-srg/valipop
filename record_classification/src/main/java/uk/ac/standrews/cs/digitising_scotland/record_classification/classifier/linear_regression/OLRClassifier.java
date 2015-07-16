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

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author frjd2, jkc25
 */
public class OLRClassifier implements Classifier {

    private static final long serialVersionUID = -2561454096763303789L;
    private static final double STATIC_CONFIDENCE = 0.89;

    private OLRCrossFold model = null;
    private Properties properties;
    private VectorFactory vector_factory;

    public void setModel(OLRCrossFold model) {
        this.model = model;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public OLRCrossFold getModel() {
        return model;
    }

    public Properties getProperties() {
        return properties;
    }

    public OLRClassifier() {

        model = new OLRCrossFold();
        properties = MachineLearningConfiguration.getDefaultProperties();
    }

    public void train(final Bucket bucket) {

        if (vector_factory == null) {
            CodeIndexer index = new CodeIndexer(bucket);
            vector_factory = new VectorFactory(bucket, index);
            List<NamedVector> trainingVectorList = getTrainingVectors(bucket);
            Collections.shuffle(trainingVectorList);
            model = new OLRCrossFold(trainingVectorList, properties);

        } else {
            int classCountDiff = getNumClassesAdded(bucket);
            int featureCountDiff = getFeatureCountDiff(bucket);
            Matrix matrix = expandModel(featureCountDiff, classCountDiff);
            List<NamedVector> trainingVectorList = getTrainingVectors(bucket);
            Collections.shuffle(trainingVectorList);
            model = new OLRCrossFold(trainingVectorList, properties, matrix);
        }

        model.train();
    }

    @Override
    public Classification classify(String data) {

        if (vector_factory == null) {
            return Classification.UNCLASSIFIED;
        } else {
            TokenSet tokenSet = new TokenSet(data);
            final String description = tokenSet.toString();
            NamedVector vector = vector_factory.createNamedVectorFromString(description, "unknown");
            Vector classifyFull = model.classifyFull(vector);
            int classificationID = classifyFull.maxValueIndex();
            String code = vector_factory.getCodeIndexer().getCode(classificationID);

            return new Classification(code, tokenSet, STATIC_CONFIDENCE);
        }
    }

    @Override
    public String getName() {

        return getClass().getSimpleName();
    }

    @Override
    public String getDescription() {

        return "Classifies using online logistic regression";
    }

    @Override
    public String toString() {

        return getName();
    }

    private ArrayList<NamedVector> getTrainingVectors(final Bucket bucket) {

        ArrayList<NamedVector> trainingVectorList = new ArrayList<>();

        for (Record record : bucket) {
            final List<NamedVector> listOfVectors = vector_factory.generateVectorsFromRecord(record);
            trainingVectorList.addAll(listOfVectors);
        }
        return trainingVectorList;
    }

    private Matrix expandModel(final int featureCountDiff, final int classCountDiff) {

        return MatrixEnlarger.enlarge(model.getAverageBetaMatrix(), featureCountDiff, classCountDiff);
    }

    private int getFeatureCountDiff(final Bucket bucket) {

        int initNoFeatures = vector_factory.getNumberOfFeatures();
        vector_factory.updateDictionary(bucket);
        int newNoFeatures = vector_factory.getNumberOfFeatures();
        return newNoFeatures - initNoFeatures;
    }

    private int getNumClassesAdded(final Bucket bucket) {

        int initNoClasses = vector_factory.getCodeIndexer().getNumberOfOutputClasses();
        vector_factory.getCodeIndexer().addGoldStandardCodes(bucket);
        int newNoClasses = vector_factory.getCodeIndexer().getNumberOfOutputClasses();
        return newNoClasses - initNoClasses;
    }
}
