package populationStatistics.dataDistributionTables.selfCorrecting;

import populationStatistics.dataDistributionTables.DataDistribution;
import populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import populationStatistics.dataDistributionTables.statsKeys.StatsKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ProportionalDistributionAdapter extends DataDistribution {

    MultipleDeterminedCount determineCount(StatsKey key);
    void returnAchievedCount(MultipleDeterminedCount achievedCount);

}
