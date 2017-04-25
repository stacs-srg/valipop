package populationStatistics.dataDistributionTables.selfCorrecting;

import populationStatistics.dataDistributionTables.DataDistribution;
import populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import utils.specialTypes.LabeledValueSet;
import utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ProportionalDistributionAdapter extends DataDistribution {

    MultipleDeterminedCount determineCount(StatsKey key);
    void returnAchievedCount(DeterminedCount<LabeledValueSet<IntegerRange, Integer>> achievedCount);

}
