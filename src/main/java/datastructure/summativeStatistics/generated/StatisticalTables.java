package datastructure.summativeStatistics.generated;

import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.UnsupportedDateConversion;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface StatisticalTables {

    /**
     * Gets the survivor table for the given event across the specified utils.time period. A survivor table gives the number
     * in the risk group in the startYear and then for each year shows how many people remain in the risk group but to
     * whom the event has not happened since the startYear.
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
     * @param startYear  the start year
     * @param timePeriod the utils.time period
     * @param event      the event
     * @return the survivor table
     */
    OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event);


    OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event, Double scalingFactor) throws UnsupportedDateConversion;

}
