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
