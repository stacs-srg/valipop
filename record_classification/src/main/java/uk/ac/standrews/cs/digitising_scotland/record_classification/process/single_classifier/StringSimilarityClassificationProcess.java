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

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.util.*;

import java.io.*;

public class StringSimilarityClassificationProcess extends AbstractClassificationProcess {

    private final StringSimilarityMetric similarity_metric;

    public StringSimilarityClassificationProcess(StringSimilarityMetric similarity_metric, InputStreamReader gold_standard_data_reader, double training_ratio, int number_of_repetitions) throws Exception {

        super(gold_standard_data_reader, training_ratio, number_of_repetitions);
        this.similarity_metric = similarity_metric;
    }

    @Override
    public Classifier getClassifier() {

        return new StringSimilarityClassifier(similarity_metric);
    }

    @Override
    public String getClassifierDescription() {

        return "string-similarity";
    }

    @Override
    public Cleaner getCleaner() {

        return ConsistentCodingCleaner.CORRECT;
    }
}
