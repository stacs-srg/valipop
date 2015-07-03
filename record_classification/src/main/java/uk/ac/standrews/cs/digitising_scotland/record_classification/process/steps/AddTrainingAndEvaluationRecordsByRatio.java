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

import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;

/**
 * Adds a randomly selected ratio of records from a gold standard to the training records, and adds the remaining to the evaluation records of a given {@link Context context}.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public class AddTrainingAndEvaluationRecordsByRatio implements Step {

    private static final long serialVersionUID = 6192497012225048336L;
    private static final double MIN_RATIO = 0.0;
    private static final double MAX_RATIO = 1.0;
    private final double training_ratio;
    private final Bucket gold_standard;
    private final Cleaner[] cleaners;

    /**
     * Instantiates a new step which randomly selects a ratio of records from the gold standard as the training records, and the remaining as the evaluation records in the {@link Context context} of a classification process.
     *
     * @param gold_standard the gold standard data from which to select training and evaluation records
     * @param training_ratio the ratio of gold standard records to be used for training
     * @param cleaners the cleaners by which to clean the gold standard records prior to adding training and evaluation records to the context
     * @throws IllegalArgumentException if the given ratio is not within inclusive range of {@code 0.0}  to {@code 1.0}.
     */
    public AddTrainingAndEvaluationRecordsByRatio(Bucket gold_standard, double training_ratio, Cleaner... cleaners) throws IllegalArgumentException {

        validateRatio(training_ratio);
        this.gold_standard = gold_standard;
        this.training_ratio = training_ratio;
        this.cleaners = cleaners;
    }

    private void validateRatio(final double ratio) {

        if (ratio < MIN_RATIO || ratio > MAX_RATIO) {
            throw new IllegalArgumentException("ratio must be within inclusive range of 0.0 to 1.0");
        }
    }

    @Override
    public void perform(final Context context) throws Exception {

        final Bucket training_records;
        final Bucket evaluation_records;
        final Bucket cleaned_gold_standard = cleanGoldStandard();

        if (training_ratio == MIN_RATIO) {
            training_records = new Bucket();
            evaluation_records = cleaned_gold_standard;
        }
        else if (training_ratio == MAX_RATIO) {
            training_records = cleaned_gold_standard;
            evaluation_records = new Bucket();
        }
        else {
            training_records = cleaned_gold_standard.randomSubset(context.getRandom(), training_ratio);
            evaluation_records = cleaned_gold_standard.difference(training_records);
        }

        context.addTrainingRecords(training_records);
        context.addEvaluationRecords(evaluation_records);
    }

    private Bucket cleanGoldStandard() throws Exception {

        Bucket cleaned = gold_standard;
        if (cleaners != null) {
            for (Cleaner cleaner : cleaners) {
                cleaned = cleaner.clean(cleaned);
            }
        }
        return cleaned;
    }
}
