package uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.StatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.StringToIntegerSet;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MultipleDeterminedCountByString extends MultipleDeterminedCount<String, String, String> {

    public MultipleDeterminedCountByString(StatsKey key, LabelledValueSet<String, Integer> determinedCount,
                                           LabelledValueSet<String, Double> rawCorrectedCount,
                                           LabelledValueSet<String, Double> rawUncorrectedCount) {

        super(key, determinedCount, rawCorrectedCount, rawUncorrectedCount);
    }

    @Override
    public LabelledValueSet<String, Integer> getZeroedCountsTemplate(RandomGenerator random) {
        return new StringToIntegerSet(determinedCount.getLabels(), 0, random);
    }
}
