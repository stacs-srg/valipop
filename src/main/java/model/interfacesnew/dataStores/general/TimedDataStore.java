package model.interfacesnew.dataStores.general;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface TimedDataStore {

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

}
