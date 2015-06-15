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

import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InconsistentCodingException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InvalidCodeException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.UnclassifiedGoldStandardRecordException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.UnknownDataException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.util.List;

/**
 * Classification process using a single classifier.
 *
 * @author Graham Kirby
 */
public interface ClassificationProcess {

    /**
     * Returns a description of the classifier used in the classification process.
     * @return the classifier description
     */
    String getClassifierDescription();

    void setInfoLevel(InfoLevel info_level);

    /**
     * Trains the classifier, runs it with the non-training data, and evaluates the quality of the results over a number of repetitions.
     *
     * @throws IOException                             if the file containing the gold standard or classified records cannot be read.
     * @throws InvalidCodeException                    if a code in the classified records does not appear in the gold standard records
     * @throws UnknownDataException                    if a record in the classified records contains data that does not appear in the gold standard records
     * @throws InconsistentCodingException             if there exist multiple gold standard records containing the same data and different classifications
     * @throws UnclassifiedGoldStandardRecordException if a record in the gold standard records is not classified
     */
    DataSet trainClassifyAndEvaluate() throws Exception;

    /**
     * Evaluates the quality of the classification on each run with respect to the original gold standard classification.
     *
     * @return the classification quality metrics for each run
     * @throws InvalidCodeException                    if a code in the classified records does not appear in the gold standard records
     * @throws UnknownDataException                    if a record in the classified records contains data that does not appear in the gold standard records
     * @throws InconsistentCodingException             if there exist multiple gold standard records containing the same data and different classifications
     * @throws UnclassifiedGoldStandardRecordException if a record in the gold standard records is not classified
     */
    List<ClassificationMetrics> getClassificationMetrics() throws Exception;

    /**
     * Calculates the confusion matrix for each run.
     * @return the confusion matrices for each run
     * @throws Exception
     */
    List<ConfusionMatrix> getConfusionMatrices() throws Exception;
}
