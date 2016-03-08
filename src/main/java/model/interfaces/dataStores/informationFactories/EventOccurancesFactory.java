package model.interfaces.dataStores.informationFactories;

import model.enums.EventType;
import model.implementation.occurrencesInformation.EventOccurrences;
import model.interfaces.dataStores.informationPassing.tableTypes.Table;

/**
 * This factory class handles the correct construction of a EventOccurrences object.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface EventOccurancesFactory {


    /**
     * Creates a EventOccurrences object.
     *
     * @return the quantified event occurrences
     */
    EventOccurrences createQuantifiedEventOccurances();

    /**
     * Calculates rate data based upon the data available in the EventOccurrences and the DemographicMakeup.
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
    void calculateImportableData(EventType variable);

    /**
     * Calculates importable data for the specified variable type for the given year in the data store.
     *
     * @param year     the given year
     * @param variable the specified variable
     */
    void calculateImportableData(int year, EventType variable);

    /**
     * Inserts the given NumberTable into the data store for the specified variable for the given year.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @param table    the NumberTable
     */
    void setData(int year, EventType variable, Table table);

}
