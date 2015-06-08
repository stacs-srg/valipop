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
package uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;

/**
 * Basic classifier interface.
 *
 * @author Graham Kirby
 */
public interface Classifier {

    /**
     * Trains the classifier on the given gold standard records.
     *
     * @param bucket the training data
     */
    void train(final Bucket bucket);

    /**
     * Classifies a single data item.
     *
     * @param data the data to be classified
     * @return the resulting classification
     */
    Classification classify(String data);

    /**
     * Classifies a bucket of data items.
     *
     * @param bucket the data to be classified
     * @return a new bucket containing the classified data
     */
    Bucket classify(final Bucket bucket);
}
