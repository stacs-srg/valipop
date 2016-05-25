package datastructure.summativeStatistics.generated;

import datastructure.summativeStatistics.PopulationComposition;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import utils.time.DateClock;

/**
 * The GeneratedPopulationComposition interface provides the functionality to be able to access the same information about the
 * simulated population as in the provided population. It also provides methods to retrieve data in the forms required
 * for the various statistical analyses that are used in the verification of the produced population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class GeneratedPopulationComposition implements PopulationComposition {

    @Override
    public DateClock getEarliestDate() {
        return null;
    }

    @Override
    public DateClock getLatestDate() {
        return null;
    }

    @Override
    public OneDimensionDataDistribution getSurvivorTable(int startYear, int timePeriod, EventType event) {
        return null;
    }
}
