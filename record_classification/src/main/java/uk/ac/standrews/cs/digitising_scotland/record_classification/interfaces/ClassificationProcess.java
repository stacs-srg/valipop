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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.util.List;

/**
 * Classification process using a single classifier.
 *
 * @author Graham Kirby
 */
public interface ClassificationProcess {

    /**
     * Returns a description of the classifier used in the classification process.
     *
     * @return the classifier description
     */
    String getClassifierDescription();

    /**
     * Sets the level of detail for the output.
     * @param info_level the level of detail
     */
    void setInfoLevel(InfoLevel info_level);

    /**
     * Trains the classifier, runs it with the non-training data, and evaluates the quality of the results over a number of repetitions.
     *
     * @throws Exception if there is an error in the training data
     */
    DataSet trainClassifyAndEvaluate() throws Exception;

    /**
     * Evaluates the quality of the classification on each run with respect to the original gold standard classification.
     *
     * @return the classification quality metrics for each run
     */
    List<ClassificationMetrics> getClassificationMetrics();

    /**
     * Calculates the confusion matrix for each run.
     *
     * @return the confusion matrices for each run
     */
    List<ConfusionMatrix> getConfusionMatrices() ;
}
