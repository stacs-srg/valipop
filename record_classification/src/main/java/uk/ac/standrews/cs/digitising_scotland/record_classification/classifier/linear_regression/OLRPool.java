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
import org.la4j.Vector;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

import static java.util.Collections.*;

/**
 * Trains a number of models on the training vectors provided. These models are then used to make predictions
 * on the testing vectors and ranked from best to worst. Only the best models are kept and used for prediction.
 *
 * @author Fraser Dunlop
 */
public class OLRPool implements Runnable, Serializable {

    private static final long serialVersionUID = -7098039612837520093L;

    private static final int POOL_SIZE = 4;
    private static final int NUMBER_OF_SURVIVORS = 2;

    private List<OLRShuffled> models = new ArrayList<>();
    private transient List<VectorFactory.NamedVector> testingVectorList = new ArrayList<>();

    /**
     * Needed for JSON deserialization.
     */
    public OLRPool() {

    }

    /**
     * Constructor.
     *
     * @param internalTrainingVectorList internal training vector list
     * @param testingVectorList internal testing vector list
     */
    public OLRPool(final List<VectorFactory.NamedVector> internalTrainingVectorList, final List<VectorFactory.NamedVector> testingVectorList, int dictionary_size, int code_map_size) {

        this.testingVectorList = testingVectorList;

        for (int i = 0; i < POOL_SIZE; i++) {
            final List<VectorFactory.NamedVector> trainingVectorList = new ArrayList<>(internalTrainingVectorList);
            OLRShuffled model = new OLRShuffled(trainingVectorList, dictionary_size, code_map_size);
            models.add(model);
        }
    }

    public OLRPool(final Matrix betaMatrix, final ArrayList<VectorFactory.NamedVector> internalTrainingVectorList, final ArrayList<VectorFactory.NamedVector> testingVectorList) {

        this.testingVectorList = testingVectorList;

        for (int i = 0; i < POOL_SIZE; i++) {
            final List<VectorFactory.NamedVector> trainingVectorList = new ArrayList<>(internalTrainingVectorList);
            OLRShuffled model = new OLRShuffled(betaMatrix, trainingVectorList);
            models.add(model);
        }
    }

    /**
     * Trains, tests, packages, sorts.
     */
    @Override
    public void run() {

        try {

            ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
            Collection<Future<?>> futures = new LinkedList<>();

            for (OLRShuffled model : models) {
                futures.add(executorService.submit(model));
            }

            Utils.handlePotentialErrors(futures);
            executorService.shutdown();
            executorService.awaitTermination(365, TimeUnit.DAYS);

        }
        catch (InterruptedException ignored) {
        }
    }

    /** Get the survivors from the model pool. */
    public List<OLRShuffled> survivors() {

        return models.stream().sorted(getDescendingCorrectlyClassifiedPortionComparator()).limit(NUMBER_OF_SURVIVORS).collect(Collectors.toList());
    }

    protected Comparator<OLRShuffled> getDescendingCorrectlyClassifiedPortionComparator() {

        return reverseOrder((one, other) -> {
            final double one_correct_ratio = getProportionTestingVectorsCorrectlyClassified(one);
            final double other_correct_ratio = getProportionTestingVectorsCorrectlyClassified(other);
            return Double.compare(one_correct_ratio, other_correct_ratio);
        });
    }

    private double getProportionTestingVectorsCorrectlyClassified(final OLRShuffled model) {

        int countCorrect = 0;
        for (VectorFactory.NamedVector vector : testingVectorList) {
            final Vector classificationVector = model.classifyFull(vector.vector);
            final int classification = getMaxValueIndex(classificationVector);

            if (Integer.parseInt(vector.name) == classification) {
                countCorrect++;
            }
        }
        return ((double) countCorrect) / ((double) testingVectorList.size());
    }

    static int getMaxValueIndex(final Vector vector) {

        double max = Double.MIN_VALUE;
        int max_index = -1;

        for (int index = 0; index < vector.length(); index++) {
            final double value = vector.get(index);
            if (value > max) {
                max = value;
                max_index = index;
            }
        }
        return max_index;
    }
}
