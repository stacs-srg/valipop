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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

import java.util.*;

/**
 * Sets the training records in the context of a classification process by randomly selecting a ratio of records in {@link Context#getGoldStandard() gold standard records}.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public class SetTrainingRecordsByRatio implements Step {

    private static final long serialVersionUID = 6192497012225048336L;
    private final double training_ratio;

    /**
     * Instantiates a new step which sets the training ratio in the context of a classification process by randomly selecting a ratio of records from the gold standard records.
     *
     * @param training_ratio the ratio of gold standard records to be used for training
     * @throws IllegalArgumentException if the given ratio is not within inclusive range of {@code 0.0}  to {@code 1.0}.
     */
    public SetTrainingRecordsByRatio(double training_ratio) throws IllegalArgumentException {

        validateRatio(training_ratio);
        this.training_ratio = training_ratio;
    }

    private void validateRatio(final double ratio) {

        if (ratio < 0.0 || ratio > 1.0) {
            throw new IllegalArgumentException("ratio must be within inclusive range of 0.0 to 1.0");
        }
    }

    @Override
    public void perform(final Context context) throws Exception {

        final Bucket gold_standard = context.getGoldStandard();
        final Bucket training_records = getRandomTrainingSubset(gold_standard, context.getRandom());

        context.setTrainingRecords(training_records);
    }

    private Bucket getRandomTrainingSubset(final Bucket bucket, final Random random) {

        Bucket subset_bucket = new Bucket();

        for (Record record : bucket) {
            if (random.nextDouble() < training_ratio) {
                subset_bucket.add(record);
            }
        }

        return subset_bucket;
    }
}
