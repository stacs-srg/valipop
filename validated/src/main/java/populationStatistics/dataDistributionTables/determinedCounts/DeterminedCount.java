package populationStatistics.dataDistributionTables.determinedCounts;

import populationStatistics.dataDistributionTables.statsKeys.StatsKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeterminedCount {

    private StatsKey key;
    int determinedCount;

    int fufilledCount;

    public DeterminedCount(StatsKey key, int determinedCount) {
        this.key = key;
        this.determinedCount = determinedCount;
    }

    public int getDeterminedCount() {
        return determinedCount;
    }

    public StatsKey getKey() {
        return key;
    }

    public int getFufilledCount() {
        return fufilledCount;
    }

    public void setFufilledCount(int fufilledCount) {
        this.fufilledCount = fufilledCount;
    }
}
