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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

/**
 * Trains a classifier in the context of a classification process using the {@link Context#getTrainingRecords() training records} in the context .
 *
 * @author Masih Hajiarab Derkani
 */
public class TrainClassifier implements Step {

    private static final long serialVersionUID = 5825366701064269040L;

    @Override
    public void perform(final Context context) throws Exception {

        final Classifier classifier = context.getClassifier();
        final Bucket training_records = context.getTrainingRecords();
        classifier.train(training_records);
    }
}
