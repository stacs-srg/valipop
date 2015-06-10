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
package uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces;

import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Classification process using a single classifier.
 *
 * @author Graham Kirby
 */
public interface ClassificationProcess {

    /**
     * Sets the level of detail to be output.
     *
     * @param verbose the level of detail
     */
    void setInfoLevel(InfoLevel verbose);

    /**
     * Sets the gold standard data to be used.
     *
     * @param gold_standard_data_reader a reader for the gold standard data
     */
    void setGoldStandardData(InputStreamReader gold_standard_data_reader);

    /**
     * Sets the training ratio to be used.
     *
     * @param training_ratio the approximate proportion of the gold standard data that should be used for training
     */
    void setTrainingRatio(double training_ratio);

    /**
     * Trains the classifier using the gold standard data.
     */
    void performTraining();

    /**
     * Classifies the non-training portion of the gold standard data.
     */
    void performClassification();

    /**
     * Evaluates the quality of the classification with respect to the original gold standard classification.
     *
     * @return the classification quality metrics
     * @throws InvalidCodeException                    if a code in the classified records does not appear in the gold standard records
     * @throws UnknownDataException                    if a record in the classified records contains data that does not appear in the gold standard records
     * @throws InconsistentCodingException             if there exist multiple gold standard records containing the same data and different classifications
     * @throws UnclassifiedGoldStandardRecordException if a record in the gold standard records is not classified
     */
    ClassificationMetrics evaluateClassification() throws InvalidCodeException, UnknownDataException, InconsistentCodingException, UnclassifiedGoldStandardRecordException;

    /**
     * Trains the classifier, runs it with the non-training data, and evaluates the quality of the results over a number of repetitions.
     *
     * @param number_of_repetitions the number of times to repeat the process
     * @throws IOException                             if the file containing the gold standard or classified records cannot be read.
     * @throws InvalidCodeException                    if a code in the classified records does not appear in the gold standard records
     * @throws UnknownDataException                    if a record in the classified records contains data that does not appear in the gold standard records
     * @throws InconsistentCodingException             if there exist multiple gold standard records containing the same data and different classifications
     * @throws UnclassifiedGoldStandardRecordException if a record in the gold standard records is not classified
     */
    DataSet trainClassifyAndEvaluate(int number_of_repetitions) throws IOException, InvalidCodeException, UnknownDataException, InconsistentCodingException, UnclassifiedGoldStandardRecordException, InputFileFormatException;
}
