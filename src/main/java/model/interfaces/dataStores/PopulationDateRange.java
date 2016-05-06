package model.interfaces.dataStores;

import model.time.DateClock;

/**
 * The PopulationDateRange interface provides high level common information about the information
 * collections found in the model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationDateRange {

    /**
     * Gets earliest day that this Data Store is required to provide information regarding.
     *
     * @return the earliest day
     */
    DateClock getEarliestDay();

    /**
     * Gets latest day that this Data Store is required to provide information regarding.
     *
     * @return the latest day
     */
    DateClock getLatestDay();


}
