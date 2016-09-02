package datastructure.summativeStatistics.generated;

import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPerson;
import model.IPopulation;
import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.UnsupportedDateConversion;
import utils.time.YearDate;

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
    OneDimensionDataDistribution getCohortSurvivorTable(Date cohortYear, EventType event) throws UnsupportedEventType;

    OneDimensionDataDistribution getCohortSurvivorTable(Date cohortYear, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedDateConversion, UnsupportedEventType;

    OneDimensionDataDistribution getTimePeriodSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) throws UnsupportedEventType, UnsupportedDateConversion;

    OneDimensionDataDistribution getTimePeriodSurvivorTable(Date startYear, int ageLimit, EventType event) throws UnsupportedEventType;

    Collection<FailureTimeRow> getFailureAtTimesTable(Date year, String denoteGroupAs, Date simulationEndDate, EventType event);

    Collection<FailureTimeRow> getFailureAtTimesTable(Date year, String denoteGroupAs, Date simulationEndDate, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedDateConversion;

    Collection<YearDate> getDataYearsInMap(EventType maleDeath);
}
