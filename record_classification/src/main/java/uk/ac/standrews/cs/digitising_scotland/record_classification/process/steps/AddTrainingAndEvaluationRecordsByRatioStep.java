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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.Step;

/**
 * Adds a randomly selected ratio of records from a gold standard to the training records, and adds the remaining to the evaluation records of a given {@link ClassificationContext context}.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public class AddTrainingAndEvaluationRecordsByRatioStep implements Step {

    private static final long serialVersionUID = 6192497012225048336L;
    private static final double MIN_RATIO = 0.0;
    private static final double MAX_RATIO = 1.0;
    private final double training_ratio;

    /**
     * Instantiates a new step which randomly selects a ratio of records from the gold standard as the training records, and the remaining as the evaluation records in the {@link ClassificationContext context} of a classification process.
     *
     * @param training_ratio the proportion of gold standard records to be used for training
     */
    public AddTrainingAndEvaluationRecordsByRatioStep(double training_ratio) {

        validateRatio(training_ratio);
        this.training_ratio = training_ratio;
    }

    @Override
    public void perform(final ClassificationContext context) {

        final Bucket gold_standard_records = context.getGoldStandardRecords();

        if (training_ratio == MIN_RATIO) {

            context.addEvaluationRecords(gold_standard_records);
        }
        else if (training_ratio == MAX_RATIO) {

            context.addTrainingRecords(gold_standard_records);
        }
        else {
            context.addTrainingRecords(gold_standard_records.randomSubset(context.getRandom(), training_ratio));
            context.addEvaluationRecords(gold_standard_records.difference(context.getTrainingRecords()));
        }
    }

    private void validateRatio(final double ratio) {

        if (ratio < MIN_RATIO || ratio > MAX_RATIO) {
            throw new IllegalArgumentException("ratio must be within inclusive range of 0.0 to 1.0");
        }
    }
}
