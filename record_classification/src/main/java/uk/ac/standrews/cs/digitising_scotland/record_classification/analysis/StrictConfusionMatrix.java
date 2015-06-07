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
package uk.ac.standrews.cs.digitising_scotland.record_classification.analysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InconsistentCodingException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InvalidCodeException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.UnclassifiedGoldStandardRecordException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.UnknownDataException;

/**
 * Confusion matrix using exact matching on codes.
 *
 * @author Fraser Dunlop
 * @author Graham Kirby
 */
public class StrictConfusionMatrix extends AbstractConfusionMatrix {

    public StrictConfusionMatrix(final Bucket classified_records, final Bucket gold_standard_records, boolean check_classification_consistency) throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        super(classified_records, gold_standard_records, check_classification_consistency);
    }

    @Override
    protected boolean classificationsMatch(String asserted_code, String real_code) {
        return asserted_code.equals(real_code);
    }
}
