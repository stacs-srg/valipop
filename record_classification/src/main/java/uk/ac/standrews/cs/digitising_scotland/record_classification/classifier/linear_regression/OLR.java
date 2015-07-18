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

import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.function.Functions;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An online logistic regression model that allows standard SGD or per term annealing with the option of
 * pseudo Bayesian L1 prior regularisation.
 */
public class OLR implements Serializable {

    private static final long serialVersionUID = 4157757308558382483L;

    /**
     * The minimum permitted value for the log likelihood.
     */
    private static final double LOGLIK_MINIMUM = -100.0;

    /**
     * The learning rate.
     */
    private static final double LEARNING_RATE = 100;

    /**
     * The decay rates for per term annealing - allows each feature its own learning.
     */
    private static final double ANNEALING_RATE = 0.999;

    private static int default_number_of_features;
    private static int default_number_of_categories;

    private int numFeatures;

    private int number_of_categories;

    private SerializableDenseMatrix beta;

    private int[] updateCounts;

    private int step;

    private volatile double runningLogLikelihood;

    private volatile AtomicInteger numLogLikelihoodSumUpdates;

    private volatile AtomicLong numTrained;

    public OLR() {
    }

    public void init(int dictionary_size, int code_map_size) {

        default_number_of_features = dictionary_size;
        default_number_of_categories = code_map_size;

        init2();
        initBeta();
        init3();
    }

    /**
     * Instantiates a new olr.
     *
     * @param beta the beta
     */
    public void init(final Matrix beta) {

        init2();
        initBeta(beta.clone());
        init3();
    }

    private void init2() {

        numFeatures = default_number_of_features;
        number_of_categories = default_number_of_categories;

        resetRunningLogLikelihood();
    }

    private void initBeta() {

        beta = new SerializableDenseMatrix(number_of_categories - 1, numFeatures);
    }

    /**
     * Initialise model with new beta matrix. Sets the number of rows and cols to size of beta.
     *
     * @param beta the beta
     */
    private void initBeta(final Matrix beta) {

        this.beta = new SerializableDenseMatrix(beta);

        numFeatures = beta.numCols();
        number_of_categories = beta.numRows() + 1;
    }

    private void init3() {

        updateCounts = new int[numFeatures];
    }

    /**
     * Gets the step.
     *
     * @return the step
     */
    public int getStep() {

        return step;
    }

    /**
     * Gets the beta.
     *
     * @return the beta
     */
    public SerializableDenseMatrix getBeta() {

        return beta;
    }

    /**
     * Classifies an instance vector and returns a result vector.
     *
     * @param instance the instance vector to classify
     * @return the result vector
     */
    public Vector classifyFull(final Vector instance) {

        Vector r = new DenseVector(number_of_categories);
        r.viewPart(1, number_of_categories - 1).assign(classify(instance));
        r.setQuick(0, 1.0 - r.zSum());
        return r;
    }

    /**
     * Reset running log likelihood.
     */
    public void resetRunningLogLikelihood() {

        runningLogLikelihood = 0.;
        numLogLikelihoodSumUpdates = new AtomicInteger(1);
        numTrained = new AtomicLong(0);
    }

    /**
     * Update log likelihood sum.
     *
     * @param actual         the actual
     * @param classification the classification
     */
    private void updateLogLikelihoodSum(final int actual, final Vector classification) {

        double thisloglik;

        if (actual > 0) {
            final double classificationGet = classification.get(actual - 1);
            final double mathLog = Math.log(classificationGet);
            thisloglik = Math.max(LOGLIK_MINIMUM, mathLog);
        } else {
            thisloglik = Math.max(LOGLIK_MINIMUM, Math.log1p(-classification.zSum()));
        }

        if (numLogLikelihoodSumUpdates.get() != 0) {
            runningLogLikelihood += (thisloglik - runningLogLikelihood) / numLogLikelihoodSumUpdates.get();
        } else {
            runningLogLikelihood = thisloglik;
        }

        numLogLikelihoodSumUpdates.getAndIncrement();
    }

    /**
     * Trains an OLR model on a instance vector with a known 'actual' output class.
     *
     * @param instance feature vector
     */
    public void train(final NamedVector instance) {

        numTrained.getAndIncrement();
        updateModelParameters(instance);
        updateCounts(instance);
        nextStep();
    }

    /**
     * Next step.
     */
    private void nextStep() {

        step++;
    }

    /**
     * Update model parameters.
     *
     * @param instance the instance
     */
    private void updateModelParameters(final NamedVector instance) {

        Vector gradient = calcGradient(instance);
        for (int category = 0; category < number_of_categories - 1; category++) {
            updateBetaCategory(instance, gradient, category);
        }
    }

    private Vector calcGradient(final NamedVector instance) {

        int actual = Integer.parseInt(instance.getName());

        // what does the current model say?
        Vector v = classify(instance);
        updateLogLikelihoodSum(actual, v);
        Vector r = v.like();

        if (actual != 0) {
            r.setQuick(actual - 1, 1);
        }
        r.assign(v, Functions.MINUS);

        return r;
    }

    /**
     * Update beta category.
     *
     * @param instance the instance
     * @param gradient the gradient
     * @param category the category
     */
    private void updateBetaCategory(final Vector instance, final Vector gradient, final int category) {

        double gradientBase = gradient.get(category);
        Iterable<Element> element = instance.nonZeroes();
        for (Element nonZeroFeature : element) {
            updateCategoryAtNonZeroFeature(category, gradientBase, nonZeroFeature);
        }
    }

    /**
     * Update category at non zero feature.
     *
     * @param category       the category
     * @param gradientBase   the gradient base
     * @param featureElement the feature element
     */
    private void updateCategoryAtNonZeroFeature(final int category, final double gradientBase, final Element featureElement) {

        final double aSmallNumber = 0.000001;
        int feature = featureElement.index();

        if (gradientBase > aSmallNumber || gradientBase < -aSmallNumber) {
            updateCoefficient(category, feature, featureElement, gradientBase);
        }
    }

    /**
     * Update coefficient.
     *
     * @param category       the category
     * @param feature        the feature
     * @param featureElement the feature element
     * @param gradientBase   the gradient base
     */
    private void updateCoefficient(final int category, final int feature, final Element featureElement, final double gradientBase) {

        double newValue = beta.getQuick(category, feature) + gradientBase * getLearningRate(feature) * featureElement.get();
        beta.setQuick(category, feature, newValue);
    }

    /**
     * Gets the learning rate.
     *
     * @param feature the feature
     * @return the learning rate
     */
    private double getLearningRate(final int feature) {

        return perTermLearningRate(feature);
    }

    /**
     * Returns n-1 probabilities, one for each category but the 0-th.  The probability of the 0-th
     * category is 1 - sum(this result).
     *
     * @param instance A vector of features to be classified.
     * @return A vector of probabilities, one for each of the first n-1 categories.
     */
    public Vector classify(final Vector instance) {

        return link(classifyNoLink(instance));
    }

    /**
     * Link.
     *
     * @param v the v
     * @return the vector
     */
    public Vector link(final Vector v) {

        double max = v.maxValue();
        if (max >= 40) {
            // if max > 40, we subtract the large offset first
            // the size of the max means that 1+sum(exp(v)) = sum(exp(v)) to within round-off
            v.assign(Functions.minus(max)).assign(Functions.EXP);
            return v.divide(v.norm(1));
        } else {
            v.assign(Functions.EXP);
            return v.divide(1 + v.norm(1));
        }
    }

    /**
     * Classify no link.
     *
     * @param instance the instance
     * @return the vector
     */
    public Vector classifyNoLink(final Vector instance) {

        return beta.times(instance);
    }

    /**
     * Per term learning rate.
     *
     * @param j the j
     * @return the double
     */
    public double perTermLearningRate(final int j) {

        return LEARNING_RATE * Math.pow(ANNEALING_RATE, updateCounts[j]);
    }

    /**
     * Update counts and steps.
     *
     * @param instance the instance
     */
    private void updateCounts(final Vector instance) {

        Iterable<Element> instanceFeatures = instance.nonZeroes();

        for (Element feature : instanceFeatures) {
            updateCounts[feature.index()]++;
        }
    }
}
