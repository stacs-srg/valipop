package model.interfaces.dataStores.future;

import model.interfaces.dataStores.PopulationDateRange;
import model.time.DateClock;

/**
 * The PopulationStatistics holds information about the number of events that occur to a given subset of the summative
 * population. By combining this with data about the size of the given subset in the DemographicMakeup it is possible
 * to calculate a rate at which even occurs for a given subset of the population which is stored in the PopulationStatistics.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EventOccurrences implements PopulationDateRange {


    @Override
    public DateClock getEarliestDate() {
        return null;
    }

    @Override
    public DateClock getLatestDate() {
        return null;
    }

}
