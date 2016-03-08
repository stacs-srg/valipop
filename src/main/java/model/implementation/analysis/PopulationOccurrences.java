package model.implementation.analysis;

import model.enums.EventType;
import model.enums.Gender;
import model.interfaces.dataStores.PopulationInformationCollection;
import model.interfaces.dataStores.informationAccess.EventRateTables;
import model.interfaces.dataStores.informationAccess.StatisticalTables;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;
import model.interfaces.dataStores.informationPassing.tableTypes.TwoWayTable;

/**
 * The PopulationOccurrences interface provides the functionality to be able to access the same information about the
 * simulated population as in the provided population. It also provides methods to retrieve data in the forms required
 * for the various statistical analyses that are used in the verification of the produced population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationOccurrences implements PopulationInformationCollection, StatisticalTables {

    @Override
    public int getEarliestDay() {
        return 0;
    }

    @Override
    public int getLatestDay() {
        return 0;
    }

    @Override
    public OneWayTable<Integer> getSurvivorTable(int startYear, int timePeriod, EventType event) {
        return null;
    }
}
