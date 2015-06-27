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

import old.record_classification_old.tools.Utils;
import old.record_classification_old.tools.configuration.MachineLearningConfiguration;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

/**
 * Distributes training vectors across {@link OLRPool}s in a cross fold manner. Allows concurrent training
 * of the {@link OLRPool}s and provides a classify method that averages the classifications given by each pool.
 *
 * @author fraserdunlop, jkc25
 */
public class OLRCrossFold implements Serializable {

    private static final long serialVersionUID = -749333540672669562L;

    /** The Logger. */
    private static transient final Logger LOGGER = LoggerFactory.getLogger(OLRCrossFold.class);

    /** The OLRPool models. */
    private List<OLRPool> models = new ArrayList<>();

    /** The number of cross folds. */
    private int folds;

    /** The classifier. */
    private OLR classifier;

    public OLRCrossFold() {

    }

    /**
     * Constructs an OLRCrossFold object with the given trainingVectors.
     *
     * @param trainingVectorList training vectors to use when training/validating each fold.
     * @param properties         properties
     */
    public OLRCrossFold(final List<NamedVector> trainingVectorList, final Properties properties) {

        ArrayList<NamedVector>[][] trainingVectors = init(trainingVectorList, properties);
        for (int i = 0; i < this.folds + 1; i++) {
            OLRPool model = new OLRPool(properties, trainingVectors[i][0], trainingVectors[i][1]);
            models.add(model);
        }
    }

    /**
     * Constructs an OLRCrossFold object with the given trainingVectors.
     *
     * @param trainingVectorList training vectors to use when training/validating each fold.
     * @param properties         properties properties file.
     * @param betaMatrix        betaMatrix this matrix contains the betas and will be propagated down to the lowest OLR object.
     */
    public OLRCrossFold(final List<NamedVector> trainingVectorList, final Properties properties, final Matrix betaMatrix) {

        ArrayList<NamedVector>[][] trainingVectors = init(trainingVectorList, properties);
        for (int i = 0; i < this.folds + 1; i++) {
            OLRPool model = new OLRPool(properties, betaMatrix, trainingVectors[i][0], trainingVectors[i][1]);
            models.add(model);
        }
    }

    private ArrayList<NamedVector>[][] init(final List<NamedVector> trainingVectorList, final Properties properties) {

        folds = Integer.parseInt(properties.getProperty("OLRFolds"));
        final int foldWarningThreshold = 20;
        if (folds > foldWarningThreshold) {
            LOGGER.info("You have selected a large value of OLRfolds. Please check that you meant to do this. It may harm performance");
        }
        return CrossFoldFactory.make(trainingVectorList, folds);
    }

    /**
     * Gets the average running log likelihood.
     *
     * @return the average running log likelihood
     */
    public double getAverageRunningLogLikelihood() {

        double ll = 0.;
        for (OLRPool model : models) {
            ll += model.getAverageRunningLogLikelihood();
        }
        ll /= models.size();
        return ll;
    }

    /**
     * Gets the number of records used for training so far across all the models in the pool.
     * @return int the number of training records used so far
     */
    public long getNumTrained() {

        long numTrained = 0;
        for (OLRPool model : models) {
            numTrained += model.getNumTrained();
        }
        return numTrained;
    }

    /**
     * Resets running log likelihoods.
     */
    public void resetRunningLogLikelihoods() {

        for (OLRPool model : models) {
            model.resetRunningLogLikelihoods();
        }
    }

    /**
     * Stops training on all models in the {@link OLRPool}.
     */
    public void stop() {

        for (OLRPool model : models) {
            model.stop();
        }
    }

    /**
     * Trains all the OLR models contained in this OLRCrossfold.
     */
    public void train() {

        try {
            this.trainAllModels();
        }
        catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Train all models.
     *
     * @throws InterruptedException the interrupted exception
     * @throws ExecutionException
     */
    private void trainAllModels() throws InterruptedException, ExecutionException {

        ExecutorService stopService = Executors.newFixedThreadPool(1);
        ExecutorService executorService = Executors.newFixedThreadPool(folds);
        Collection<Future<?>> futures = new LinkedList<>();

        for (OLRPool model : models) {
            futures.add(executorService.submit(model));
        }

        Utils.handlePotentialErrors(futures);
        executorService.shutdown();
        final int timeout = 365;
        executorService.awaitTermination(timeout, TimeUnit.DAYS);

        prepareClassifier();
    }

    /**
     * Prepares the averaged OLR classifier for use by finding the top performing models and averaging their beta matrices.
     */
    private void prepareClassifier() {

        List<OLRShuffled> survivors = getSurvivors();
        Matrix classifierMatrix = getClassifierMatrix(survivors);
        classifier = new OLR(MachineLearningConfiguration.getDefaultProperties(), classifierMatrix);
    }

    /**
     * Returns the averaged beta matrix for this OLRCrossfold. If the OLRCrossfold has not been trained than an empty matrix will be returned.
     *
     * @return the averaged beta matrix for this OLRCrossfold, or an empty matrix if no training has been done.
     */
    public Matrix getAverageBetaMatrix() {

        return classifier.getBeta();
    }

    /**
     * Gets the survivors for each of the {@link OLRShuffled} models.
     *
     * @return the survivors
     */
    private List<OLRShuffled> getSurvivors() {

        List<OLRShuffled> survivors = new ArrayList<>();
        for (OLRPool model : models) {
            survivors.addAll(model.getSurvivors());
        }
        return survivors;
    }

    /**
     * Gets the classifier matrix.
     *
     * @param survivors the survivors
     * @return the classifier matrix
     */
    private Matrix getClassifierMatrix(final List<OLRShuffled> survivors) {

        Stack<Matrix> matrices = new Stack<>();
        for (OLRShuffled model : survivors) {
            matrices.add(model.getBeta());
        }
        Matrix classifierMatrix = matrices.pop();
        while (!matrices.empty()) {
            classifierMatrix = classifierMatrix.plus(matrices.pop());
        }
        classifierMatrix = classifierMatrix.divide(survivors.size());
        return classifierMatrix;
    }

    /**
     * Classifies a vector.
     *
     * @param instance vector
     * @return vector encoding probability distribution over output classes
     */
    public Vector classifyFull(final Vector instance) {

        return classifier.classifyFull(instance);
    }

    /**
     * Gets the log likelihood averaged over the models in the pool.
     * @param actual the actual classification
     * @param instance the instance vector
     * @return log likelihood
     */
    public double logLikelihood(final int actual, final Vector instance) {

        return classifier.logLikelihood(actual, instance);
    }
}
