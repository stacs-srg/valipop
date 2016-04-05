package model.interfaces.dataStores.future;

import model.enums.EventType;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ImportData {

    /**
     * Calculates rate data based upon the data available in the DesiredPopulationComposition and the DemographicMakeup.
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

}
