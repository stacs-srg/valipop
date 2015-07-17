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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Training involves shuffling the training vectors and training the model. This process is repeated for the desired
 * number of repetitions.
 *
 * @author fraserdunlop
 */
public class OLRShuffled implements Runnable, Serializable {

    private static final long serialVersionUID = -4877057661440167819L;

    private OLR model;
    private static final int NUMBER_OF_REPETITIONS = 10;
    private transient List<NamedVector> trainingVectorList = new ArrayList<>();
    private boolean stopped = false;

    /**
     * Constructor.
     *
     * @param trainingVectorList2 the training vector list
     */
    public OLRShuffled(final List<NamedVector> trainingVectorList2) {

        this.trainingVectorList = trainingVectorList2;
        model = new OLR();
        model.init();
    }

    /**
     * Constructor.
     *
     * @param betaMatrix         the new beta matrix which is used as a starting point for model training
     * @param trainingVectorList the training vector list
     */
    public OLRShuffled(final Matrix betaMatrix, final List<NamedVector> trainingVectorList) {

        this.trainingVectorList = trainingVectorList;
        this.model = new OLR();
        model.init(betaMatrix);
    }

    public OLRShuffled() {

    }

    /**
     * Gets the running log likelihood.
     *
     * @return the running log likelihood
     */
    public double getRunningLogLikelihood() {

        return model.getRunningLogLikelihood();
    }

    /**
     * Reset running log likelihood.
     */
    public void resetRunningLogLikelihood() {

        model.resetRunningLogLikelihood();
    }

    /**
     * Allows train() to be run in its own thread.
     */
    @Override
    public void run() {

        trainIfPossible();
    }

    /**
     * This method performs the training of the model.
     * Shuffles the training files, trains on all files and repeats this process.
     */
    private void train() {

        for (int rep = 0; rep < NUMBER_OF_REPETITIONS; rep++) {
            if (stopped()) {
                break;
            }
            shuffleAndTrainOnAllVectors();
        }
    }

    /**
     * Classifies a vector.
     *
     * @param instance vector to classify
     * @return vector encoding probability distribution over output classes
     */
    public Vector classifyFull(final Vector instance) {

        return model.classifyFull(instance);
    }

    /**
     * Gets the log likelihood.
     *
     * @param actual   actual classification
     * @param instance instance vector
     * @return log likelihood
     */
    public double logLikelihood(final int actual, final Vector instance) {

        return model.logLikelihood(actual, instance);
    }

    //
    //    /**
    //     * Gets the configuration options.
    //     *
    //     * @return the configuration options
    //     */
    //    private void getConfigOptions() {
    //
    //        NUMBER_OF_REPETITIONS = Integer.parseInt(properties.getProperty("OLRShuffledReps"));
    //    }

    /**
     * Checks if the models are trainable and trains the models if possible.
     */
    private void trainIfPossible() {

        this.train();
    }

    /**
     * Shuffle and train on all vectors.
     */
    private void shuffleAndTrainOnAllVectors() {

        for (NamedVector vector : trainingVectorList) {
            if (stopped()) {
                break;
            }
            this.model.train(vector);
        }

    }

    /**
     * Sets the 'stopped' flag to true.
     */
    public void stop() {

        stopped = true;
    }

    /**
     * Returns the value of the stopped flag.
     *
     * @return true, if stopped
     */
    private boolean stopped() {

        return stopped;
    }

//    /**
//     * Gets the number of output categories.
//     *
//     * @return int the number of categories.
//     */
//    protected int default_number_of_categories() {
//
//        return model.getNumCategories();
//    }

    /**
     * Gets the beta matrix.
     *
     * @return the beta maxtrix
     */
    protected Matrix beta() {

        return model.getBeta().getMatrix();
    }

//    /**
//     * Gets the number of records used for training so far across all the models in the pool.
//     *
//     * @return int the number of training records used so far
//     */
//    public long numTrained() {
//
//        return model.getNumTrained();
//    }

}
