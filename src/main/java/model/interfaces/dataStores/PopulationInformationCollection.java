package model.interfaces.dataStores;

import model.interfaces.dataStores.informationFlow.query.Query;
import model.interfaces.dataStores.informationFlow.result.QueryResult;

/**
 * The interface Timed data store.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationInformationCollection {

    /**
     * Gets earliest day that this Data Store is required to provide information regarding.
     *
     * @return the earliest day
     */
    int getEarliestDay();

    /**
     * Gets latest day that this Data Store is required to provide information regarding.
     *
     * @return the latest day
     */
    int getLatestDay();


    QueryResult getInfo(Query query);



}
