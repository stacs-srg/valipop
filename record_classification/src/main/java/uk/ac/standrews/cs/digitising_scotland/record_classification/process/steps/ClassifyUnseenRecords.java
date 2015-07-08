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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;

/**
 * Classifies unseen records and stores the results in a classification process {@link ClassificationContext context}.
 *
 * @author Masih Hajiarab Derkani
 */
public class ClassifyUnseenRecords implements Step {

    private static final long serialVersionUID = 292143932733171808L;
    private final Bucket unseen_records;

    /**
     * Instantiates a new unseen record classification step.
     *
     * @param unseen_records the unseen records to classify
     */
    public ClassifyUnseenRecords(Bucket unseen_records) {

        this.unseen_records = unseen_records;
    }

    @Override
    public void perform(final ClassificationContext context) throws Exception {

        final Classifier classifier = context.getClassifier();
        final Bucket classified_unseen_records = classifier.classify(unseen_records);

        context.setClassifiedUnseenRecords(classified_unseen_records);
    }
}
