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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;

import java.io.IOException;
import java.io.InputStreamReader;

public class ExactMatchClassificationProcess extends AbstractClassificationProcess {

    public static final int NUMBER_OF_REPETITIONS = 5;

    public ExactMatchClassificationProcess() {

        super();
    }

    public ExactMatchClassificationProcess(String[] args) throws IOException, InvalidArgException {

        super(args);
    }

    public ExactMatchClassificationProcess(InputStreamReader gold_standard_data_reader, double training_ratio) {

        super(gold_standard_data_reader, training_ratio);
    }

    public static void main(final String[] args)  {

        try {
            ExactMatchClassificationProcess classification_process = new ExactMatchClassificationProcess(args);

            classification_process.setInfoLevel(InfoLevel.SUMMARY);
            classification_process.trainClassifyAndEvaluate(NUMBER_OF_REPETITIONS);
        }
        catch (Exception e) {
            System.out.println("problem in classification process: " + e.getMessage());
        }
    }

    @Override
    public Classifier getClassifier() {

        return new ExactMatchClassifier();
    }
}
