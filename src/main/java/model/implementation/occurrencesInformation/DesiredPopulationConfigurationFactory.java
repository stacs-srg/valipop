package model.implementation.occurrencesInformation;

import model.enums.EventType;
import model.implementation.occurrencesInformation.DesiredPopulationComposition;
import model.interfaces.dataStores.informationPassing.tableTypes.Table;

/**
 * This factory class handles the correct construction of a DesiredPopulationComposition object.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DesiredPopulationConfigurationFactory {


    /**
     * Creates a DesiredPopulationComposition object.
     *
     * @return the quantified event occurrences
     */
    DesiredPopulationComposition createQuantifiedEventOccurances() {

        return null;
    }

    /**
     * Inserts the given NumberTable into the data store for the specified variable for the given year.
     *
     * @param year     the given year
     * @param variable the specified variable
     * @param table    the NumberTable
     */
    void setData(int year, EventType variable, Table table) {

    }

}
