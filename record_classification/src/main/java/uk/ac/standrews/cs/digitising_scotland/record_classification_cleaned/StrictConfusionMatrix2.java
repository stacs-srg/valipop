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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.tools.Utils;

import java.util.Set;

/**
 * This is the 'classic' true confusion matrix. Predictions are only correct if they
 * exactly match the gold standard code.
 *
 * @author Fraser Dunlop
 * @author Graham Kirby
 */
public class StrictConfusionMatrix2 extends AbstractConfusionMatrix2 {

    public StrictConfusionMatrix2(final Bucket2 classified_records, final Bucket2 gold_standard_records) throws InvalidCodeException, InconsistentCodingException, UnknownDataException {

        super(classified_records, gold_standard_records);
    }

    /**
     * True pos and false neg.
     *
     * @param predictedClassifications the set code triples
     * @param goldStandardClassifications the gold standard triples
     */
    protected void truePosAndFalseNeg(final Set<Classification> predictedClassifications, final Set<Classification> goldStandardClassifications) {

//        for (Classification goldStandardCode : goldStandardClassifications) {
//            final Code code = goldStandardCode.getCode();
//            int codeID = index.getID(code);
//            if (contains(code, predictedClassifications)) {
//                true_positive_counts[codeID]++;
//            }
//            else {
//                falseNegative[codeID]++;
//            }
//        }

    }

    /**
     * Total and false pos.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void totalAndFalsePos(final Set<Classification> setCodeTriples, final Set<Classification> goldStandardTriples) {

//        for (Classification predictedCode : setCodeTriples) {
//            final Code code = predictedCode.getCode();
//            int codeID = index.getID(code);
//            classification_counts[codeID]++;
//            if (!contains(code, goldStandardTriples)) {
//                false_positive_counts[codeID]++;
//            }
//        }
    }

    /**
     * Returns true is a code is in the specified set of CodeTriples.
     * @param code code to check for
     * @param setCodeTriples set to check in
     * @return true if present
     */
    public boolean contains(final Code code, final Set<Classification> setCodeTriples) {

        return Utils.contains(code, setCodeTriples);
    }

}
