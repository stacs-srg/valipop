package populationStatistics.dataDistributionTables.determinedCounts;

import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import utils.MapUtils;
import utils.specialTypes.IntegerRangeToIntegerSet;
import utils.specialTypes.LabeledValueSet;
import utils.specialTypes.integerRange.IntegerRange;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MultipleDeterminedCount implements DeterminedCount<LabeledValueSet<IntegerRange, Integer>> {

    private StatsKey key;
    LabeledValueSet<IntegerRange, Integer> determinedCount;

    LabeledValueSet<IntegerRange, Integer> fufilledCount;

    public MultipleDeterminedCount(StatsKey key, LabeledValueSet<IntegerRange, Integer> determinedCount) {
        this.key = key;
        this.determinedCount = determinedCount;
    }

    public LabeledValueSet<IntegerRange, Integer> getDeterminedCount() {
        return determinedCount;
    }

    public StatsKey getKey() {
        return key;
    }

    public LabeledValueSet<IntegerRange, Integer> getFufilledCount() {
        return fufilledCount;
    }

    public void setFufilledCount(LabeledValueSet<IntegerRange, Integer> fufilledCount) {
        this.fufilledCount = fufilledCount;
    }

    public LabeledValueSet<IntegerRange, Integer> getZeroedCountsTemplate() {
        return new IntegerRangeToIntegerSet(determinedCount.getLabels(), 0);
    }

}
