package model.interfaces.dataStores.informationFactories;

import model.enums.VariableType;
import model.interfaces.dataStores.informationFlow.result.returnTable.NumberTable;

/**
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
    void setData(int year, VariableType variable, NumberTable table);

}
