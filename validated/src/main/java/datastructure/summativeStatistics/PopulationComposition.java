package datastructure.summativeStatistics;

import datastructure.summativeStatistics.generated.StatisticalTables;
import utils.time.DateBounds;

/**
 * The PopulationComposition interface provides the functionality to be able to access the same information about a
 * population regardless of its origin. It also provides methods to retrieve data in the forms required for the various
 * statistical analyses that are used in the verification of the produced population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationComposition extends DateBounds, StatisticalTables {

}
