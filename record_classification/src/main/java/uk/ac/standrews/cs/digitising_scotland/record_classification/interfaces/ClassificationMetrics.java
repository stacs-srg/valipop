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

import java.util.Map;

/**
 * Collection of metrics measuring the effectiveness of classification.
 *
 * @author Graham Kirby
 */
public interface ClassificationMetrics {

    /**
     * Returns a map from classification class to precision.
     * @return the map
     */
    Map<String, Double> getPerClassPrecision();

    /**
     * Returns a map from classification class to recall.
     * @return the map
     */
    Map<String, Double> getPerClassRecall();

    /**
     * Returns a map from classification class to accuracy.
     * @return the map
     */
    Map<String, Double> getPerClassAccuracy();

    /**
     * Returns a map from classification class to F1 measure.
     * @return the map
     */
    Map<String, Double> getPerClassF1();

    /**
     * Returns the macro-averaged precision over all classes.
     * @return the macro-averaged precision
     */
    double getMacroAveragePrecision();

    /**
     * Returns the macro-averaged recall over all classes.
     * @return the macro-averaged recall
     */
    double getMacroAverageRecall();

    /**
     * Returns the macro-averaged accuracy over all classes.
     * @return the macro-averaged accuracy
     */
    double getMacroAverageAccuracy();

    /**
     * Returns the macro-averaged F1 measure over all classes.
     * @return the macro-averaged F1 measure
     */
    double getMacroAverageF1();

    /**
     * Returns the micro-averaged precision over all classes.
     * @return the micro-averaged precision
     */
    double getMicroAveragePrecision();

    /**
     * Returns the micro-averaged recall over all classes.
     * @return the micro-averaged recall
     */
    double getMicroAverageRecall();

    /**
     * Returns the micro-averaged accuracy over all classes.
     * @return the micro-averaged accuracy
     */
    double getMicroAverageAccuracy();

    /**
     * Returns the micro-averaged F1 measure over all classes.
     * @return the micro-averaged F1 measure
     */
    double getMicroAverageF1();

    /**
     * Prints out the metrics at a specified level of detail.
     * @param info_level the detail level
     */
    void printMetrics(InfoLevel info_level);

    /**
     * Prints out the metrics at summary level of detail.
     */
    void printMetrics();
}
