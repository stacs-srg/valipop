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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Cleans the gold standard records in a classification process context.
 *
 * @author Masih Hajiarab Derkani
 */
public class CleanTrainingAndEvaluationRecords implements Step {

    private static final long serialVersionUID = -4959580121867002858L;
    private final List<Cleaner> cleaners;

    /**
     * Instantiates a new step that cleans the gold standard records in the context of a classification process.
     *
     * @param cleaner the cleaner by which to perform the cleaning
     * @param cleaners the other optional cleaners by which to perform the cleaning on the cleaned records
     */
    public CleanTrainingAndEvaluationRecords(Cleaner cleaner, Cleaner... cleaners) {

        this.cleaners = new ArrayList<>();
        populateCleaners(cleaner, cleaners);
    }

    private void populateCleaners(final Cleaner cleaner, final Cleaner[] cleaners) {

        this.cleaners.add(cleaner);
        if (cleaners != null) {
            this.cleaners.addAll(Arrays.asList(cleaner));
        }
    }

    @Override
    public void perform(final Context context) throws Exception {

        cleanTrainingRecords(context);
        cleanEvaluationRecords(context);
    }

    private void cleanEvaluationRecords(final Context context) throws Exception {

        final Bucket evaluation_records = context.getEvaluationRecords();

        if (evaluation_records != null) {

            final Bucket cleaned = clean(evaluation_records);
            context.setEvaluationRecords(cleaned);
        }
    }

    private void cleanTrainingRecords(final Context context) throws Exception {

        final Bucket training_records = context.getTrainingRecords();

        if (training_records != null) {

            final Bucket cleaned = clean(training_records);
            context.setTrainingRecords(cleaned);
        }
    }

    private Bucket clean(final Bucket bucket) throws Exception {

        Bucket cleaned = bucket;

        for (Cleaner cleaner : cleaners) {
            cleaned = cleaner.clean(cleaned);
        }

        return cleaned;
    }
}
