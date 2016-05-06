package model.interfaces.dataStores.informationAccess;

import model.enums.EventType;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface StatisticalTables {

    /**
     * Gets the survivor table for the given event across the specified time period. A survivor table gives the number
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
     * @param timePeriod the time period
     * @param event      the event
     * @return the survivor table
     */
    OneWayTable<Integer> getSurvivorTable(int startYear, int timePeriod, EventType event);

}
