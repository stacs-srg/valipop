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
import java.util.Random;

/**
 * Represents a classification process that consists of a list of steps and produces a {@link Bucket bucket}  of classified records.
 *
 * @author Masih Hajiarab Derkani
 */
public class ClassificationProcessWithContext extends ClassificationProcess implements Serializable {

    private static final long serialVersionUID = -456456546456L;

    private final ClassificationContext context;

    /**
     * Instantiates a new classification process.
     *
     * @param random the random number generator
     */
    public ClassificationProcessWithContext(final ClassifierFactory factory, final Random random) {

        super(factory, random);
        context = new ClassificationContext(factory.get(), random);
    }

    public ClassificationProcessWithContext(final Classifier classifier, final Random random) {

        this(() -> classifier, random);
    }

    /**
     * Sequentially performs the steps in this classification process.
     *
     * @return the classified records, or {@code null} if no records were classified
     * @throws Exception if an error while performing the process steps
     */
    public Bucket call() throws Exception {

        return super.call(context);
    }

    /**
     * Gets the context of this classification process.
     *
     * @return the context of this classification process
     */
    public ClassificationContext getContext() {

        return context;
    }

    public ClassificationMetrics getClassificationMetrics() {

        return context.getClassificationMetrics();
    }

    public ConfusionMatrix getConfusionMatrix() {

        return context.getConfusionMatrix();
    }
}
