package datastructure.summativeStatistics;

import utils.time.Date;

/**
 * The DateBounds interface provides high level common information about the information
 * collections found in the model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DateBounds {

    /**
     * Gets earliest day that this Data Store is required to provide information regarding.
     *
     * @return the earliest day
     */
    Date getStartDate();

    /**
     * Gets latest day that this Data Store is required to provide information regarding.
     *
     * @return the latest day
     */
    Date getEndDate();


}
