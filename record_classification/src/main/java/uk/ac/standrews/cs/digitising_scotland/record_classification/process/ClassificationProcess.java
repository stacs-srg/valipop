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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Represents a classification process that consists of a list of steps and produces a {@link Bucket bucket}  of classified records.
 *
 * @author Masih Hajiarab Derkani
 */
public class ClassificationProcess implements Callable<Bucket>, Serializable {

    private static final long serialVersionUID = -3086230162106640193L;

    private final Context context;
    private final List<Step> steps;

    /**
     * Instantiates a new classification process.
     *
     * @param random the random number generator
     */
    public ClassificationProcess(final Classifier classifier, final Random random) {

        context = new Context(classifier, random);
        steps = new ArrayList<>();
    }

    /**
     * Adds a step to the steps to be performed by this process.
     *
     * @param step the step to be performed in the classification process.
     * @return this classification process to accommodate chaining of step additions.
     */
    public void addStep(Step step) {

        steps.add(step);
    }

    /**
     * Sequentially performs the steps in this classification process.
     *
     * @return the classified records, or {@code null} if no records were classified
     * @throws Exception if an error while performing the process steps
     */
    @Override
    public Bucket call() throws Exception {

        for (Step step : steps) {
            step.perform(context);
        }

        return context.getClassifiedUnseenRecords();
    }

    /**
     * Gets the context of this classification process.
     *
     * @return the context of this classification process
     */
    public Context getContext() {

        return context;
    }

    public ClassificationMetrics getClassificationMetrics() {

        return context.getClassificationMetrics();
    }

    public ConfusionMatrix getConfusionMatrix() {

        return context.getConfusionMatrix();
    }
}
