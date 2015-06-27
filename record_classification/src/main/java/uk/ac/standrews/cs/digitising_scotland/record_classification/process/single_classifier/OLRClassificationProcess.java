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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.single_classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.ConsistentCodingCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Cleaner;

import java.io.InputStreamReader;

public class OLRClassificationProcess extends AbstractClassificationProcess {

    @SuppressWarnings("WeakerAccess")
    public OLRClassificationProcess(String[] args) throws Exception {

        super(args);
    }

    public OLRClassificationProcess(InputStreamReader gold_standard_data_reader, double training_ratio, int number_of_repetitions) throws Exception {

        super(gold_standard_data_reader, training_ratio, number_of_repetitions);
    }

    @Override
    public Classifier getClassifier() {

        return new OLRClassifier();
    }

    @Override
    public String getClassifierDescription() {

        return "OLR";
    }

    @Override
    public Cleaner getCleaner() {

        return ConsistentCodingCleaner.CORRECT;
    }
}
