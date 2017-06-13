package populationStatistics.dataDistributionTables.determinedCounts;

import populationStatistics.dataDistributionTables.statsKeys.StatsKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SingleDeterminedCount implements DeterminedCount<Integer> {

    private StatsKey key;
    int determinedCount;

    int fufilledCount;

    public SingleDeterminedCount(StatsKey key, int determinedCount) {
        this.key = key;
        this.determinedCount = determinedCount;
    }

    public Integer getDeterminedCount() {
        return determinedCount;
    }

    public StatsKey getKey() {
        return key;
    }

    public Integer getFufilledCount() {
        return fufilledCount;
    }

    public void setFufilledCount(Integer fufilledCount) {
        this.fufilledCount = fufilledCount;
    }
}
