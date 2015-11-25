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
import org.la4j.vector.*;
import org.la4j.vector.functor.VectorFunction;

import java.io.*;
import java.util.concurrent.atomic.*;

/**
 * An online logistic regression model.
 */
public class OLR implements Serializable {

    private static final long serialVersionUID = 4157757308558382483L;

    /** The minimum permitted value for the log likelihood. */
    private static final double LOGLIK_MINIMUM = -100.0;

    /** The learning rate. */
    private static final double LEARNING_RATE = 100;

    /** The decay rates for per term annealing - allows each feature its own learning. */
    private static final double ANNEALING_RATE = 0.999;
    private static final double DELTA = 0.000001;
    private static final VectorFunction ABS = (index, value) -> Math.abs(value);
    private static final VectorFunction EXP = (index, value) -> Math.exp(value);

    private int number_of_categories;

    private SerializableDenseMatrix beta;

    private int[] updateCounts;

    private volatile double runningLogLikelihood;

    private volatile AtomicInteger numLogLikelihoodSumUpdates;

    private volatile AtomicLong numTrained;

    public OLR() {

    }

    public OLR(int dictionary_size, int code_map_size) {

        init(new SerializableDenseMatrix(code_map_size - 1, dictionary_size), dictionary_size, code_map_size);
    }

    public OLR(final Matrix matrix) {

        init(new SerializableDenseMatrix(matrix.copy()), matrix.columns(), matrix.rows() + 1);

    }

    /**
     * Gets the beta.
     *
     * @return the beta
     */
    protected SerializableDenseMatrix getBeta() {

        return beta;
    }

    /**
     * Classifies an instance vector and returns a result vector.
     *
     * @param instance the instance vector to classify
     * @return the result vector
     */
    protected Vector classifyFull(final Vector instance) {

        final DenseVector vector = DenseVector.zero(number_of_categories);
        final Vector classified_instance = classify(instance);
        vector.set(0, 1.0 - classified_instance.sum());

        for (int i = 1; i < number_of_categories; i++) {
            vector.set(i, classified_instance.get(i - 1));
        }

        return vector;
    }

    /**
     * Trains an OLR model on a instance vector with a known 'actual' output class.
     *
     * @param instance feature vector
     */
    protected void train(final VectorFactory.NamedVector instance) {

        numTrained.getAndIncrement();
        updateModelParameters(instance);
        updateCounts(instance.vector);
    }

    private void init(SerializableDenseMatrix beta, int number_of_features, int number_of_categories) {

        this.beta = beta;

        runningLogLikelihood = 0.;
        numLogLikelihoodSumUpdates = new AtomicInteger(1);
        numTrained = new AtomicLong(0);

        this.number_of_categories = number_of_categories;

        updateCounts = new int[number_of_features];
    }

    private void updateLogLikelihoodSum(final int actual, final Vector classification) {

        double thisloglik;

        if (actual > 0) {
            final double classificationGet = classification.get(actual - 1);
            final double mathLog = Math.log(classificationGet);
            thisloglik = Math.max(LOGLIK_MINIMUM, mathLog);
        }
        else {
            thisloglik = Math.max(LOGLIK_MINIMUM, Math.log1p(-classification.sum()));
        }

        if (numLogLikelihoodSumUpdates.get() != 0) {
            runningLogLikelihood += (thisloglik - runningLogLikelihood) / numLogLikelihoodSumUpdates.get();
        }
        else {
            runningLogLikelihood = thisloglik;
        }

        numLogLikelihoodSumUpdates.getAndIncrement();
    }

    private void updateModelParameters(final VectorFactory.NamedVector instance) {

        Vector gradient = calcGradient(instance);
        for (int category = 0; category < number_of_categories - 1; category++) {
            updateBetaCategory(instance.vector, gradient, category);
        }
    }

    private Vector calcGradient(final VectorFactory.NamedVector instance) {

        int actual = Integer.parseInt(instance.name);

        // what does the current model say?
        Vector classified_instance = classify(instance.vector);
        updateLogLikelihoodSum(actual, classified_instance);
        Vector blank = classified_instance.blank();

        if (actual != 0) {
            blank.set(actual - 1, 1);
        }

        return blank.transform((index, value) -> value - classified_instance.get(index));
    }

    private void updateBetaCategory(final Vector instance, final Vector gradient, final int category) {

        final double gradient_base = gradient.get(category);

        if (gradient_base > DELTA || gradient_base < -DELTA) {
            instance.each((index, value) -> {
                if (value != 0.0) {
                    updateCoefficient(category, index, value, gradient_base);
                }
            });
        }
    }

    private void updateCoefficient(final int category, final int index, final double value, final double gradient_base) {

        double new_value = beta.get(category, index) + gradient_base * getLearningRate(index) * value;
        beta.set(category, index, new_value);
    }

    private double getLearningRate(final int feature) {

        return LEARNING_RATE * Math.pow(ANNEALING_RATE, updateCounts[feature]);
    }

    /**
     * Returns n-1 probabilities, one for each category but the 0-th.  The probability of the 0-th
     * category is 1 - sum(this result).
     *
     * @param instance A vector of features to be classified.
     * @return A vector of probabilities, one for each of the first n-1 categories.
     */
    private Vector classify(final Vector instance) {

        return link(beta.times(instance));
    }

    private Vector link(final Vector vector) {

        final double max = vector.max();
        if (max >= 40) {
            // if max > 40, we subtract the large offset first
            // the size of the max means that 1+sum(exp(v)) = sum(exp(v)) to within round-off
            vector.transform((index, value) -> value - max).transform(EXP);
            return vector.divide(norm_1(vector));
        }
        else {
            vector.transform(EXP);
            return vector.divide(1 + norm_1(vector));
        }
    }

    private double norm_1(final Vector vector) {
        // sum of absolute values is equal to Lp Space with p of 1, i.e. norm(1) in Mahout API
        return vector.transform(ABS).sum();
    }

    private void updateCounts(final Vector instance) {

        instance.each((index, value) -> {
            if (value != 0.0) {
                updateCounts[index]++;
            }
        });
    }
}
