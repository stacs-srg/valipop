package model.interfacesnew.dataStores.values;

import model.enums.VariableType;
import model.interfacesnew.dataStores.general.CheckableDataStore;
import model.interfacesnew.dataStores.general.ImputableDataStore;
import model.interfacesnew.dataStores.general.TimedDataStore;
import model.interfacesnew.dataStores.query.TableQuery;
import model.interfacesnew.dataStores.query.ValueQuery;

/**
 * The RateDataStore holds data about the rate at which specified events occur to specified subsets of members of the
 * summative population.
 * <p>
 * This data can be drawn by calculation (provided by methods in this interface, rather than calculations within a
 * single store which are provided in the ImputableDataStore interface) from the Demographic and Summative Data Stores
 * or, in the case where data is already in rate form, inserted directly into the RateDateStore.
 * <p>
 * Methods (in the CheckableDataStore interface) are provided to check for similarity between the rates and what rates
 * would would be calculable using data in the other stores (where present) and a preference can be set as to whether to
 * prioritise the use of calculated rate data (when present) over specified rate data - although default behaviour is to
 * use specified rate data.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface RateDataStore extends TimedDataStore, CheckableDataStore, ImputableDataStore {

    /**
     * Enforces the use of calcukated rate data even in the presence of specified rate data that has been inserted into
     * the data store. The default behaviour is that specified rate data should be used.
     *
     * @param b boolean indicating the enforcement of the use of calculated data
     */
    void enforceCalculatedOverSpecified(boolean b);

    /**
     * Retrieves the specific rate data for the given query.
     *
     * @param query the given query
     * @return the specific rate data
     */
    double getSpecificRateData(ValueQuery query);

    /**
     * Returns the rate data for the specified variable for all years.
     *
     * @param variable the specified variable
     * @return the set of NumberTables representing the data for each year for the specified variable
     */
    NumberTable[] getRateData(VariableType variable);

    /**
     * Returns the rate data for the specified variable for the given year.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @return the NumberTable representing the data for the given year for the specified variable
     */
    NumberTable getRateData(int year, VariableType variable);

    /**
     * Inserts the given NumberTable into the data store for the specified variable for the given year.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @param table    the NumberTable
     */
    void setData(int year, VariableType variable, NumberTable table);

    /**
     * Calculates rate data based upon the data available in the SummativeDataStore and the DemographicDataStore.
     */
    void calculateImportableData();

    /**
     * Calculates importable data for the given year.
     *
     * @param year the given year
     */
    void calculateImportableData(int year);

    /**
     * Calculates importable data for the specified variable type across all years in the data store.
     *
     * @param variable the specified variable
     */
    void calculateImportableData(VariableType variable);

    /**
     * Calculates importable data for the specified variable type for the given year in the data store.
     *
     * @param year     the given year
     * @param variable the specified variable
     */
    void calculateImportableData(int year, VariableType variable);


    /**
     * Returns a NumberTable containing the event table for the given query. For example the query may be the number of
     * people who die each year who were born in a given year.
     *
     * @param query the query
     * @return the number table
     */
    NumberTable deriveSummativeEventCountTable(TableQuery query);

}
