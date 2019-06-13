package uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.StatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MultipleDeterminedCountByIR extends MultipleDeterminedCount<IntegerRange, Integer, Integer> {

    public MultipleDeterminedCountByIR(StatsKey<Integer, Integer> key, LabelledValueSet<IntegerRange, Integer> determinedCount,
                                   LabelledValueSet<IntegerRange, Double> rawCorrectedCount,
                                   LabelledValueSet<IntegerRange, Double> rawUncorrectedCount) {

        super(key, determinedCount, rawCorrectedCount, rawUncorrectedCount);
    }

    @Override
    public LabelledValueSet<IntegerRange, Integer> getZeroedCountsTemplate(RandomGenerator random) {
        return new IntegerRangeToIntegerSet(determinedCount.getLabels(), 0, random);
    }
}
