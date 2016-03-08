package model.interfaces.dataStores.informationFactories;

import model.enums.EventType;
import model.interfaces.dataStores.informationPassing.tableTypes.Table;

/**
 * The SetEventOccurrences interface provides a standard approach for the insertion of data into the information
 * collections in the model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface SetEventOccurrences {

    /**
     * Inserts the given NumberTable into the data store for the specified variable for the given year.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @param table    the NumberTable
     */
    void setData(int year, EventType variable, Table table);

}
