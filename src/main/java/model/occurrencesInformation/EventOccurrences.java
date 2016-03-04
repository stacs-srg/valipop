package model.occurrencesInformation;

import model.interfacesnew.dataStores.PopulationInformationCollection;
import model.interfacesnew.dataStores.informationFlow.query.Query;
import model.interfacesnew.dataStores.informationFlow.result.QueryResult;

/**
 * The EventOccurrences holds information about the number of events that occur to a given subset of the summative
 * population. By combining this with data about the size of the given subset in the DemographicMakeup it is possible
 * to calculate a rate at which even occurs for a given subset of the population which is stored in the QuantifiedEventOccurrences.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EventOccurrences implements PopulationInformationCollection {


    @Override
    public int getEarliestDay() {
        return 0;
    }

    @Override
    public int getLatestDay() {
        return 0;
    }

    @Override
    public QueryResult getInfo(Query query) {
        return null;
    }
}
