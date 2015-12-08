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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.logistic_regression;

import org.apache.mahout.classifier.*;
import org.apache.mahout.classifier.sgd.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author masih
 */
class AdaptiveOLRClassifier extends OLRClassifier {

    public static final int DEFAULT_TRAINING_ITERATIONS = 30;
    private static final long serialVersionUID = 468659239591358547L;

    protected final int training_iteration;
    private final Random random;

    public AdaptiveOLRClassifier() {

        this(DEFAULT_TRAINING_ITERATIONS, null);
    }

    public AdaptiveOLRClassifier(int training_iteration) {

        this(training_iteration, null);
    }

    public AdaptiveOLRClassifier(int training_iteration, final Random random) {

        this.training_iteration = training_iteration;
        this.random = random;
    }

    protected AbstractVectorClassifier train(final List<OnlineTrainingRecord> training_records) {

        final PriorFunction regularisation = new L2();
        final int categories = countCategories();
        final int features = countFeatures();
        final AdaptiveLogisticRegression model = new AdaptiveLogisticRegression(categories, features, regularisation);

        train(training_records, model);

        return model.getBest().getPayload().getLearner();
    }

    protected void train(final List<OnlineTrainingRecord> training_records, final OnlineLearner model) {

        final int training_records_size = training_records.size();
        final int total_training_steps = training_records_size * DEFAULT_TRAINING_ITERATIONS;

        resetTrainingProgressIndicator(total_training_steps);

        final Random random = getRandom();
        try {
            for (int i = 0; i < training_iteration; i++) {

                Collections.shuffle(training_records, random);
                training_records.forEach(record -> train(model, record));
            }
        }
        finally {
            model.close();
        }
    }

    protected void train(final OnlineLearner model, final OnlineTrainingRecord record) {

        model.train(record.id, record.code, record.feature_vector);
        progressTrainingStep();
    }

    protected Random getRandom() {

        return isDeterministic() ? random : ThreadLocalRandom.current();
    }

    private boolean isDeterministic() {return this.random != null;}
}
