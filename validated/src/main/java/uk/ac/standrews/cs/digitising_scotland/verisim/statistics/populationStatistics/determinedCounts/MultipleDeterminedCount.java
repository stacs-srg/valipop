/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.determinedCounts;

import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsKeys.StatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.labeledValueSets.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.labeledValueSets.IntegerRangeToIntegerSet;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MultipleDeterminedCount implements DeterminedCount<LabeledValueSet<IntegerRange, Integer>, LabeledValueSet<IntegerRange, Double>> {

    private StatsKey key;
    LabeledValueSet<IntegerRange, Integer> determinedCount;

    LabeledValueSet<IntegerRange, Integer> fufilledCount;

    LabeledValueSet<IntegerRange, Double> rawCorrectedCount;
    LabeledValueSet<IntegerRange, Double> rawUncorrectedCount;

    public MultipleDeterminedCount(StatsKey key, LabeledValueSet<IntegerRange, Integer> determinedCount,
                                   LabeledValueSet<IntegerRange, Double> rawCorrectedCount,
                                   LabeledValueSet<IntegerRange, Double> rawUncorrectedCount) {
        this.key = key;
        this.determinedCount = determinedCount;
        this.rawCorrectedCount = rawCorrectedCount;
        this.rawUncorrectedCount = rawUncorrectedCount;
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

    @Override
    public LabeledValueSet<IntegerRange, Double> getRawCorrectedCount() {
        return rawCorrectedCount;
    }

    @Override
    public LabeledValueSet<IntegerRange, Double> getRawUncorrectedCount() {
        return rawUncorrectedCount;
    }

    public void setFufilledCount(LabeledValueSet<IntegerRange, Integer> fufilledCount) {
        this.fufilledCount = fufilledCount;
    }

    public LabeledValueSet<IntegerRange, Integer> getZeroedCountsTemplate() {
        return new IntegerRangeToIntegerSet(determinedCount.getLabels(), 0);
    }

}
