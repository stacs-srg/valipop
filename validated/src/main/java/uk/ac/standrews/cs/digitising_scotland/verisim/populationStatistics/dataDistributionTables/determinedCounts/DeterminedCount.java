package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts;

import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DeterminedCount<T> {

    StatsKey getKey();

    T getDeterminedCount();

    void setFufilledCount(T fufilledCount);

    T getFufilledCount();

}
