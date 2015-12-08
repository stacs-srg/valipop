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
 * @author Masih Hajiarab Derkani
 */
public class CrossFoldOLRClassifier extends AdaptiveOLRClassifier {

    public static final int DEFAULT_FOLDS = 4;
    private static final long serialVersionUID = 5607653225734742032L;
    private final int folds;

    public CrossFoldOLRClassifier() {

        this(DEFAULT_FOLDS, DEFAULT_TRAINING_ITERATIONS, null);
    }

    public CrossFoldOLRClassifier(int folds, final int training_iterations) {

        this(folds, training_iterations, null);
    }

    public CrossFoldOLRClassifier(final int folds, final int training_iterations, final Random random) {

        super(training_iterations, random);
        this.folds = folds;
    }

    protected AbstractVectorClassifier train(final List<OnlineTrainingRecord> training_records) {

        final PriorFunction regularisation = new L2();
        final int categories = countCategories();
        final int features = countFeatures();
        final CrossFoldLearner model = new CrossFoldLearner(folds, categories, features, regularisation);

        train(training_records, model);
        return model;
    }

    protected void trainParallel(final List<OnlineTrainingRecord> training_records, final OnlineLearner model) {

        final int training_records_size = training_records.size();
        final int total_training_steps = training_records_size * training_iteration;

        resetTrainingProgressIndicator(total_training_steps);

        final List<ForkJoinTask<?>> training_iterations = new ArrayList<>();
        final ForkJoinPool pool = ForkJoinPool.commonPool();
        try {
            for (int i = 0; i < training_iteration; i++) {

                final ForkJoinTask<?> training_iteration = pool.submit(() -> {
                    shuffle(training_records).parallelStream().forEach(record -> train(model, record));
                });
                training_iterations.add(training_iteration);
            }
        }
        finally {
            model.close();
        }

        awaitCompletion(training_iterations);
    }

    private void awaitCompletion(final List<ForkJoinTask<?>> training_iterations) {

        for (ForkJoinTask<?> iteration : training_iterations) {
            try {
                iteration.get();
            }
            catch (InterruptedException error) {
                cancel(training_iterations);
                throw new RuntimeException("interrupted while awaiting training completion", error);
            }
            catch (ExecutionException error) {
                cancel(training_iterations);
                throw new RuntimeException("error while training", error.getCause());
            }
        }
    }

    private void cancel(final List<ForkJoinTask<?>> training_iterations) {

        training_iterations.forEach(iteration -> {
            iteration.cancel(true);
        });
    }

    private List<OnlineTrainingRecord> shuffle(final List<OnlineTrainingRecord> olr_training_records) {

        List<OnlineTrainingRecord> shuffled = new ArrayList<>(olr_training_records);
        Collections.shuffle(shuffled, getRandom());

        return shuffled;
    }
}
