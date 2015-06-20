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

/**
 * Cleans the gold standard records in a classification process context.
 *
 * @author Masih Hajiarab Derkani
 */
public class CleanGoldStandardRecords implements Step {

    private static final long serialVersionUID = -4959580121867002858L;
    private final Cleaner cleaner;

    /**
     * Instantiates a new step that cleans the gold standard records in the context of a classification process.
     *
     * @param cleaner the cleaner by which to perform the cleaning
     */
    public CleanGoldStandardRecords(Cleaner cleaner) {

        this.cleaner = cleaner;
    }

    @Override
    public void perform(final Context context) throws Exception {

        final Bucket gold_standard = context.getGoldStandard();
        if (gold_standard != null) {
            final Bucket cleaned_gold_standard = cleaner.clean(gold_standard);
            context.setGoldStandard(cleaned_gold_standard);
        }
    }
}
