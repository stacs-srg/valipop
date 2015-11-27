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

import org.la4j.*;
import org.la4j.matrix.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;

import static uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression.OLRPool.*;

/**
 * @author Fraser Dunlop
 * @author Jamie Carson
 * @author Graham Kirby
 */
public class OLRClassifier extends SingleClassifier {

    private static final long serialVersionUID = -2561454096763303789L;
    private static final double STATIC_CONFIDENCE = 0.89;

    private OLRCrossFold model;
    private String single_code;
    private VectorFactory vector_factory;

    public void setModel(OLRCrossFold model) {

        this.model = model;
    }

    public OLRCrossFold getModel() {

        return model;
    }

    public OLRClassifier() {

        clearModel();
    }

    public void clearModel() {

        model = new OLRCrossFold();
        vector_factory = null;
        single_code = null;
    }

    public void trainModel(final Bucket bucket) {

        resetTrainingProgressIndicator(bucket.size());

        if (vector_factory == null) {

            vector_factory = new VectorFactory(bucket);
            int number_of_distinct_tokens = vector_factory.numberOfDistinctTokens();
            int number_of_distinct_classifications = vector_factory.numberOfDistinctClassifications();

            if (number_of_distinct_classifications == 1) {

                // Need to treat this differently as the OLR algorithm doesn't work with a single output class.

                single_code = vector_factory.getCodeIndexer().getCode(0);
                return;
            }

            model = new OLRCrossFold(getTrainingVectors(bucket), number_of_distinct_tokens, number_of_distinct_classifications);

        }
        else {

            int class_count_difference = updateIndexer(bucket);
            int feature_count_difference = updateDictionary(bucket);

            Matrix matrix = enlarge(model.averageBetaMatrix(), feature_count_difference, class_count_difference);

            model = new OLRCrossFold(getTrainingVectors(bucket), matrix);
        }

        model.train();
    }

    @Override
    public Classification doClassify(String data) {

        if (vector_factory == null) {
            return Classification.UNCLASSIFIED;
        }

        TokenList token_list = new TokenList(data);

        if (single_code != null) {
            return new Classification(single_code, token_list, STATIC_CONFIDENCE, null);
        }

        VectorFactory.NamedVector vector = vector_factory.createNamedVectorFromString(token_list, "unknown");
        int classificationID = getMaxValueIndex(model.classifyFull(vector.vector));
        String code = vector_factory.getCodeIndexer().getCode(classificationID);

        return new Classification(code, token_list, STATIC_CONFIDENCE, null);
    }

    @Override
    public String getDescription() {

        return "Classifies using online logistic regression";
    }

    private ArrayList<VectorFactory.NamedVector> getTrainingVectors(final Bucket bucket) {

        ArrayList<VectorFactory.NamedVector> trainingVectorList = new ArrayList<>();

        for (Record record : bucket) {
            final List<VectorFactory.NamedVector> listOfVectors = vector_factory.generateVectorsFromRecord(record);
            trainingVectorList.addAll(listOfVectors);
            progressTrainingStep();
        }
        Collections.shuffle(trainingVectorList);
        return trainingVectorList;
    }

    private int updateDictionary(final Bucket bucket) {

        int initial_number_of_tokens = vector_factory.numberOfDistinctTokens();

        vector_factory.updateDictionary(bucket);
        return vector_factory.numberOfDistinctTokens() - initial_number_of_tokens;
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
     * @param matrix The {@link Matrix} to enlarge
     * @param additional_columns amount to expand the number of columns by
     * @param additional_rows amount to expand the number of rows by
     * @return the new expanded matrix
     */
    private static Matrix enlarge(final Matrix matrix, final int additional_columns, final int additional_rows) {

        final Matrix enlarge_matrix = DenseMatrix.zero(matrix.rows() + additional_rows, matrix.columns() + additional_columns);
        return copyInto(matrix, enlarge_matrix);
    }

    private static Matrix copyInto(final Matrix source, final Matrix destination) {

        for (int row = 0; row < source.rows(); row++) {
            for (int column = 0; column < source.columns(); column++) {
                destination.set(row, column, source.get(row, column));
            }
        }
        return destination;
    }
}
