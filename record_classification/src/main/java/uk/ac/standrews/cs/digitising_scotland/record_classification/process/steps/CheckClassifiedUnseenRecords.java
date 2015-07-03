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
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.*;

import java.util.*;

/**
 * Checks the {@link Context#getClassifiedUnseenRecords() classified unseen records} in the context of a classification process using a given {@link Checker}.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public class CheckClassifiedUnseenRecords implements Step {

    private static final long serialVersionUID = -1169844023653663515L;
    private final Checker checker;

    public CheckClassifiedUnseenRecords(Checker checker) {

        this.checker = checker;
    }

    @Override
    public void perform(final Context context) throws Exception {

        final Bucket classified_records = context.getClassifiedUnseenRecords();

        if (classified_records != null) {

            checker.check(classified_records);

        }
        else {
            //TODO warn of skipped step
        }
    }
}
