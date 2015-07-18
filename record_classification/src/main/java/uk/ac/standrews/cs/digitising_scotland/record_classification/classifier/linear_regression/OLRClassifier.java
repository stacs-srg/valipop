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

import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Fraser Dunlop
 * @author Jamie Carson
 * @author Graham Kirby
 */
public class OLRClassifier implements Classifier {

    private static final long serialVersionUID = -2561454096763303789L;
    private static final double STATIC_CONFIDENCE = 0.89;

    private OLRCrossFold model = null;

    private VectorFactory vector_factory;

    public void setModel(OLRCrossFold model) {
        this.model = model;
    }

    public OLRCrossFold getModel() {
        return model;
    }

    public OLRClassifier() {

        model = new OLRCrossFold();
    }

    public void train(final Bucket bucket) {

        if (vector_factory == null) {

            vector_factory = new VectorFactory(bucket);
            int dictionary_size = vector_factory.dictionarySize();
            int code_map_size = vector_factory.codeMapSize();
            model = new OLRCrossFold(getTrainingVectors(bucket), dictionary_size, code_map_size);

        } else {

            int class_count_difference = updateIndexer(bucket);
            int feature_count_difference = updateDictionary(bucket);

            Matrix matrix = enlarge(model.averageBetaMatrix(), feature_count_difference, class_count_difference);

            model = new OLRCrossFold(getTrainingVectors(bucket), matrix);
        }

        model.train();
    }

    @Override
    public Classification classify(String data) {

        if (vector_factory == null) {
            return Classification.UNCLASSIFIED;

        } else {
            TokenList token_list = new TokenList(data);
            NamedVector vector = vector_factory.createNamedVectorFromString(token_list, "unknown");
            Vector classifyFull = model.classifyFull(vector);
            int classificationID = classifyFull.maxValueIndex();
            String code = vector_factory.getCodeIndexer().getCode(classificationID);

            return new Classification(code, token_list, STATIC_CONFIDENCE);
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
        Collections.shuffle(trainingVectorList);
        return trainingVectorList;
    }

    private int updateDictionary(final Bucket bucket) {

        int initial_number_of_features = vector_factory.getNumberOfFeatures();

        vector_factory.updateDictionary(bucket);
        return vector_factory.getNumberOfFeatures() - initial_number_of_features;
    }

    private int updateIndexer(final Bucket bucket) {

        final CodeIndexer indexer = vector_factory.getCodeIndexer();

        int initial_number_of_classes = indexer.getNumberOfOutputClasses();
        indexer.addGoldStandardCodes(bucket);
        return indexer.getNumberOfOutputClasses() - initial_number_of_classes;
    }

    /**
     * Makes an existing matrix bigger by the amount of units specified in additionalColds and additionalRows parameters.
     * All of the content of the original matrix is preserved.
     * Rows and columns are appended on the right and bottom of the existing matrix.
     *
     * @param matrix         The {@link Matrix} to enlarge
     * @param additionalCols amount to expand the number of columns by
     * @param additionalRows amount to expand the number of rows by
     * @return the new expanded matrix
     */
    private static Matrix enlarge(final Matrix matrix, final int additionalCols, final int additionalRows) {

        Matrix largerMatrix = new DenseMatrix(matrix.numRows() + additionalRows, matrix.numCols() + additionalCols);
        return copyInto(matrix, largerMatrix);
    }

    private static Matrix copyInto(final Matrix matrix, final Matrix largerMatrix) {

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                largerMatrix.set(i, j, matrix.get(i, j));
            }
        }
        return largerMatrix;
    }
}
