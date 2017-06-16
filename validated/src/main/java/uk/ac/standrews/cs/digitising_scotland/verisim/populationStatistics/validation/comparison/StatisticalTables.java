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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.comparison;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.UnsupportedEventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.kaplanMeier.utils.FailureTimeRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulation;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface StatisticalTables {

    /**
     * Gets the survivor table for the given event across the specified utils.time period. A survivor table gives the number
     * in the risk group in the cohortYear and then for each year shows how many people remain in the risk group but to
     * whom the event has not happened since the cohortYear.
     * <p>
     * | Survivors
     * | of given
     * | event
     * ------------------
     * 0  | 12568
     * Time      1  | 10235
     * elapsed    2  |  9586
     * ... |  ...
     *
     * @param cohortYear  the start year
     * @param event      the event
     * @return the survivor table
     */
    OneDimensionDataDistribution getCohortSurvivorTable(AdvancableDate cohortYear, EventType event) throws UnsupportedEventType;

    OneDimensionDataDistribution getCohortSurvivorTable(AdvancableDate cohortYear, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedEventType;

    OneDimensionDataDistribution getTimePeriodSurvivorTable(AdvancableDate startYear, CompoundTimeUnit timePeriod, EventType event) throws UnsupportedEventType;

    OneDimensionDataDistribution getTimePeriodSurvivorTable(Date startYear, int ageLimit, EventType event) throws UnsupportedEventType;

    Collection<FailureTimeRow> getFailureAtTimesTable(AdvancableDate year, String denoteGroupAs, Date simulationEndDate, EventType event);

    Collection<FailureTimeRow> getFailureAtTimesTable(AdvancableDate year, String denoteGroupAs, Date simulationEndDate, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedEventType;

    Collection<YearDate> getDataYearsInMap(EventType maleDeath);

    OneDimensionDataDistribution getSeparationData(Date startYear, Date endYear);

    OneDimensionDataDistribution getSeparationData(AdvancableDate startYear, Date endYear, int childCap);

//    OneDimensionDataDistribution getPartneringData(Date startYear, Date endYear, IntegerRange femaleAgeRange, Set<IntegerRange> maleAgeBrackets);
//
//    SelfCorrectingProportionalDistribution getPartneringData(Date startYear, Date endYear);


}
