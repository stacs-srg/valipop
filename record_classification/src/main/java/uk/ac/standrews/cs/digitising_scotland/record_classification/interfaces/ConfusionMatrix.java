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

import java.util.Map;

/**
 * Confusion matrix representing the effectiveness of a classification process.
 *
 * @author Graham Kirby
 */
public interface ConfusionMatrix {

    /**
     * Returns the total number of classifications.
     * @return the total number
     */
    int getTotalNumberOfClassifications();

    /**
     * Returns a map from classification class to the number of records classified as that class.
     * @return the map
     */
    Map<String, Integer> getClassificationCounts();

    /**
     * Returns a map from classification class to the number of true positives for that class.
     * That is, the number of records that were classified as that class, and really were of that class.
     * @return the map
     */
    Map<String, Integer> getTruePositiveCounts();

    /**
     * Returns a map from classification class to the number of false positives for that class.
     * That is, the number of records that were classified as that class, but were not of that class.
     * @return the map
     */
    Map<String, Integer> getFalsePositiveCounts();

    /**
     * Returns a map from classification class to the number of true negatives for that class.
     * That is, the number of records that were not classified as that class, and really were not of that class.
     * @return the map
     */
    Map<String, Integer> getTrueNegativeCounts();

    /**
     * Returns a map from classification class to the number of false negatives for that class.
     * That is, the number of records that were not classified as that class, but actually were of that class.
     * @return the map
     */
    Map<String, Integer> getFalseNegativeCounts();
}
