package model.interfacesnew.analysis.population;

import model.interfacesnew.dataStores.demographic.DemographicVariables;
import model.interfacesnew.dataStores.values.NumberTable;
import model.interfacesnew.dataStores.query.TableQuery;

/**
 * The PopulationAnalysis interface provides the functionality to be able to access the same information about the
 * simulated population as is provided about the summative population. It also provides methods to retrieve data in the
 * forms required for the various statistical analyses that are used in the verification of the produced population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationAnalysis extends DemographicVariables {

    /**
     * Returns a NumberTable containing the event table for the given query. For example the query may be the number of
     * people who die each year who were born in a given year.
     *
     * @param query the query
     * @return the number table
     */
    NumberTable deriveSimulatedEventCountTable(TableQuery query);

}
