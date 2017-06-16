/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.kaplanMeier;


import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;

/**
 * This interface is used as the return form for a Kaplan-Meier analysis
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface IKaplanMeierAnalysis {

    /**
     * Returns the variable which this analysis was focused on.
     *
     * @return the variable
     */
    EventType getVariable();

    /**
     * Returns the intial year of this analysis. E.g. in the case of death the birth year of all individuals considered.
     *
     * @return the year
     */
    Date getYear();

    /**
     * Returns the log rank value.
     *
     * @return the log rank value.
     */
    double getLogRankValue();

    double getPValue();

    /**
     * Considers if the difference in the compared data is significant.
     *
     * @return if true then significant then curves are different (not what we want).
     */
    boolean significantDifferenceBetweenGroups();

    /**
     * Returns the hazard ratio. The hazard ratio is the hazard rate of the simulated population divided by the hazard
     * rate of the observed population. Is the ratio is 1 then the risk that the two populations experience are the
     * same and so we can say that for this variable that the populations are the same.
     *
     * @return the hazard ratio
     */
    double getHazardRatio();

    /**
     * Returns the the confidence interval around the hazard ratio. If the return value is R then this can be considered
     * as HR±R
     *
     * @return the hazard ratio confidence interval
     */
    double getHazardRatioConfidenceIntervals();

    /**
     * Returns true if the Hazard Ratio is significantly close to 1.
     *
     * @return true if the Hazard Ratio is significantly close to 1.
     */
    boolean significantHazardRatioSimilarity();

    /**
     * Sets confidence level. This is used to derive a z value. The confidence level should be given as a percentage
     * e.g. 90, 95, 99
     *
     * @param confidenceLevel the confidence level
     */
    void setConfidenceLevel(double confidenceLevel);

}
