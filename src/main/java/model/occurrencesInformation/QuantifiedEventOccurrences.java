package model.occurrencesInformation;

import model.interfacesnew.dataStores.PopulationInformationCollection;
import model.interfacesnew.dataStores.informationFlow.query.Query;
import model.interfacesnew.dataStores.informationFlow.result.QueryResult;

/**
 * The QuantifiedEventOccurrences holds data about the rate at which specified events occur to specified subsets of members of the
 * summative population.
 * <p>
 * This data can be drawn by calculation (provided by methods in this interface, rather than calculations within a
 * single store which are provided in the ImputeMissingOccurrences interface) from the Demographic and Summative Data Stores
 * or, in the case where data is already in rate form, inserted directly into the RateDateStore.
 * <p>
 * Methods (in the CheckOccurrencesData interface) are provided to check for similarity between the rates and what rates
 * would would be calculable using data in the other stores (where present) and a preference can be set as to whether to
 * prioritise the use of calculated rate data (when present) over specified rate data - although default behaviour is to
 * use specified rate data.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class QuantifiedEventOccurrences implements PopulationInformationCollection {


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
