/*
 * Copyright 2016 Digitising Scotland project:
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

import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.Step;

import java.util.Arrays;
import java.util.List;

public class CleanUnseenRecordsStep implements Step {

    private static final long serialVersionUID = -6593089444683420572L;
    private final Cleaner cleaner;

    public CleanUnseenRecordsStep(Cleaner cleaner) {

        this.cleaner = cleaner;
    }

    @Override
    public void perform(ClassificationContext context) {

        final Bucket cleaned_unseen_records = cleaner.apply(context.getUnseenRecords());
        context.setUnseenRecords(cleaned_unseen_records);
    }
}
