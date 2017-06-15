package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting;

import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.DataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ProportionalDistributionAdapter extends DataDistribution {

    MultipleDeterminedCount determineCount(StatsKey key);
    void returnAchievedCount(DeterminedCount<LabeledValueSet<IntegerRange, Integer>> achievedCount);

}
