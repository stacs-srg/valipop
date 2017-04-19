package populationStatistics.dataDistributionTables.determinedCounts;

import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import utils.specialTypes.integerRange.IntegerRange;

import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DeterminedCount<T> {

    StatsKey getKey();

    T getDeterminedCount();

    void setFufilledCount(T fufilledCount);

    T getFufilledCount();

}
