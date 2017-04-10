package populationStatistics.dataDistributionTables.determinedCounts;

import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import utils.MapUtils;
import utils.specialTypes.integerRange.IntegerRange;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeterminedCounts {

    private StatsKey key;
    Map<IntegerRange, Integer> determinedCount;

    Map<IntegerRange, Integer> fufilledCount;

    public DeterminedCounts(StatsKey key, Map<IntegerRange, Integer> determinedCount) {
        this.key = key;
        this.determinedCount = determinedCount;
    }

    public Map<IntegerRange, Integer> getDeterminedCount() {
        return determinedCount;
    }

    public StatsKey getKey() {
        return key;
    }

    public Map<IntegerRange, Integer> getFufilledCount() {
        return fufilledCount;
    }

    public void setFufilledCount(Map<IntegerRange, Integer> fufilledCount) {
        this.fufilledCount = fufilledCount;
    }

    public Map<IntegerRange, Integer> getZeroedCountsTemplate() {

        Map<IntegerRange, Integer> template = new HashMap<>();

        for(IntegerRange iR : determinedCount.keySet()) {
            template.put(iR, 0);
        }

        return template;

    }

}
