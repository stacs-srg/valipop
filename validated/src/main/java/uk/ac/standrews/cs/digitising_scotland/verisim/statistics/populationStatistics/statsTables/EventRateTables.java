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
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsTables;

import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsTables.dataDistributions.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsTables.dataDistributions.ProportionalDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingProportionalDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;

/**
 * The EventRateTables interface provides methods that pertain to the events modelled within the population
 * simulation. The fact they are 'quantified' means that they are expressed as rates or a standardised format where
 * otherwise indicated.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface EventRateTables {

    /**
     * Gets death rates for people at each age for the current given year of the specified gender. The death rates are
     * expressed as a proportion.
     * <p>
     *              | ASDR
     *          -------------
     *           0  | 0.203
     * Current   1  | 0.102
     * age       2  | 0.001
     *          ... |  ...
     *
     * @param year   the year
     * @param gender the gender
     * @return the death rates
     */
    OneDimensionDataDistribution getDeathRates(Date year, char gender);

    /**
     * Gets marriage rates for those married in the given year. The return table is two dimensional as it shows the rate
     * at which a male of age X marries a female of age Y in the given year.
     * <p>
     *                      Age of Male
     * <p>
     *               |   16  |   17  |   18  |  ...
     *           ------------------------------------
     *            16 | 0.003 | 0.002 | 0.200 |  ...
     * Age of     17 | 0.102 | 0.012 | 0.103 |  ...
     * female     18 | 0.109 | 0.131 | 0.171 |  ...
     *            .. |  ...  |  ...  |  ...  |
     *
     * @param year the year
     * @return the marriage rates
     */
    SelfCorrectingProportionalDistribution getPartneringRates(Date year);

    /**
     * Gets birth rates by order for births in the given year defined by the age and number of previous children born to
     * the mother.
     * <p>
     *                      Birth Order
     * <p>
     *               |   0   |   1   |   2   |  ...
     *           ------------------------------------
     *            16 | 0.003 | 0.002 | 0.000 |  ...
     * Age of     17 | 0.052 | 0.012 | 0.003 |  ...
     * female     18 | 0.109 | 0.041 | 0.021 |  ...
     *            .. |  ...  |  ...  |  ...  |
     *
     * @param year the year
     * @return the birth rates by order
     */
    SelfCorrectingTwoDimensionDataDistribution getOrderedBirthRates(Date year);

    /**
     * Gets multiple births in a maternity rate for women giving birth in a given year by age of the mother.
     * <p>
     * Number of live children resulting from maternity
     * <p>
     *               |   1   |   2   |   3   |  ...
     * ------------------------------------
     *            16 | 0.003 | 0.002 | 0.000 |  ...
     * Age of     17 | 0.052 | 0.012 | 0.003 |  ...
     * female     18 | 0.109 | 0.041 | 0.021 |  ...
     *            .. |  ...  |  ...  |  ...  |
     *
     * @param year the year
     * @return the birth rates by order
     */
    ProportionalDistribution getMultipleBirthRates(Date year);

    /**
     * Gets the rate of separation after having a given number of children as a couple. The rate is considered in respect
     * to the whole female population.
     * <p>
     *              | Rate
     *         -------------
     *           1  | 0.03
     * Current   2  | 0.02
     *   age     3  | 0.01
     *          ... |  ...
     *
     * @param year the year
     * @return the death rates
     */
    OneDimensionDataDistribution getSeparationByChildCountRates(Date year);

}
