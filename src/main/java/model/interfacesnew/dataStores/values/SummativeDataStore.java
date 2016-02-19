package model.interfacesnew.dataStores.values;

import model.enums.VariableType;
import model.interfacesnew.dataStores.general.CheckableDataStore;
import model.interfacesnew.dataStores.general.ImputableDataStore;
import model.interfacesnew.dataStores.general.TimedDataStore;
import model.interfacesnew.dataStores.query.ValueQuery;

/**
 * The SummativeDataStore holds information about the number of events that occur to a given subset of the summative
 * population. By combining this with data about the size of the given subset in the DemographicDataStore it is possible
 * to calculate a rate at which even occurs for a given subset of the population which is stored in the RateDataStore.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface SummativeDataStore extends TimedDataStore, ImputableDataStore, CheckableDataStore {

    /**
     * Retrieves the specific data for the given query.
     *
     * @param query the given query
     * @return the specific data
     */
    int getSpecificData(ValueQuery query);

    /**
     * Returns the NumberTables for all years for the specified variable.
     *
     * @param variable the specified variable
     * @return the NumberTables for all years for the given variable
     */
    NumberTable[] getData(VariableType variable);

    /**
     * Returns the NumberTables for the given year for the specified variable.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @return the data
     */
    NumberTable getData(int year, VariableType variable);

    /**
     * Sets the NumberTables for the given year for the specified variable.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @param table    the NumberTable
     */
    void setData(int year, VariableType variable, NumberTable table);

}
