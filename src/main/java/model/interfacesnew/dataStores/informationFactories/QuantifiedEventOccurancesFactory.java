package model.interfacesnew.dataStores.informationFactories;

import model.enums.VariableType;
import model.occurrencesInformation.QuantifiedEventOccurrences;

/**
 * This factory class handles the correct construction of a QuantifiedEventOccurrences object.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface QuantifiedEventOccurancesFactory extends SetEventOccurrences {


    /**
     * Creates a QuantifiedEventOccurrences object.
     *
     * @return the quantified event occurrences
     */
    QuantifiedEventOccurrences createQuantifiedEventOccurances();

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
    void calculateImportableData(VariableType variable);

    /**
     * Calculates importable data for the specified variable type for the given year in the data store.
     *
     * @param year     the given year
     * @param variable the specified variable
     */
    void calculateImportableData(int year, VariableType variable);

}
