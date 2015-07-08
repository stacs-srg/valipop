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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a classification process that consists of a list of steps and produces a {@link Bucket bucket}  of classified records.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public class ClassificationProcess implements Serializable {

    private static final long serialVersionUID = -3086230162106640193L;

    private final List<Step> steps;
    private final Random random;
    private final ClassifierFactory factory;
    private final String name;

    /**
     * Instantiates a new classification process.
     *
     * @param random the random number generator
     */
    public ClassificationProcess(final ClassifierFactory factory, final Random random) {

        steps = new ArrayList<>();
        name = factory.getClassifier().getName();
        this.random = random;
        this.factory = factory;
    }

    public Random getRandom() {

        return random;
    }

    public ClassifierFactory getClassifierFactory() {

        return factory;
    }

    public String getName() {

        return name;
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
    public Bucket call(ClassificationContext context) throws Exception {

        for (Step step : steps) {
            step.perform(context);
        }

        return context.getClassifiedUnseenRecords();
    }
}
