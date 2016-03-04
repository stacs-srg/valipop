package model.analysis;

import model.interfacesnew.dataStores.PopulationInformationCollection;
import model.interfacesnew.dataStores.informationFlow.query.Query;
import model.interfacesnew.dataStores.informationFlow.result.QueryResult;

/**
 * The PopulationAnalysis interface provides the functionality to be able to access the same information about the
 * simulated population as is provided about the summative population. It also provides methods to retrieve data in the
 * forms required for the various statistical analyses that are used in the verification of the produced population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationAnalysis implements PopulationInformationCollection {

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
