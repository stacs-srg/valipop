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
package uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts;

import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.StatsKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MultipleDeterminedCount implements DeterminedCount<LabelledValueSet<IntegerRange, Integer>, LabelledValueSet<IntegerRange, Double>> {

    private StatsKey key;

    private LabelledValueSet<IntegerRange, Integer> determinedCount;
    private LabelledValueSet<IntegerRange, Integer> fulfilledCount;

    private LabelledValueSet<IntegerRange, Double> rawCorrectedCount;
    private LabelledValueSet<IntegerRange, Double> rawUncorrectedCount;

    public MultipleDeterminedCount(StatsKey key, LabelledValueSet<IntegerRange, Integer> determinedCount,
                                   LabelledValueSet<IntegerRange, Double> rawCorrectedCount,
                                   LabelledValueSet<IntegerRange, Double> rawUncorrectedCount) {
        this.key = key;
        this.determinedCount = determinedCount;
        this.rawCorrectedCount = rawCorrectedCount;
        this.rawUncorrectedCount = rawUncorrectedCount;
    }

    public LabelledValueSet<IntegerRange, Integer> getDeterminedCount() {
        return determinedCount;
    }

    public StatsKey getKey() {
        return key;
    }

    public LabelledValueSet<IntegerRange, Integer> getFulfilledCount() {
        return fulfilledCount;
    }

    @Override
    public LabelledValueSet<IntegerRange, Double> getRawCorrectedCount() {
        return rawCorrectedCount;
    }

    @Override
    public LabelledValueSet<IntegerRange, Double> getRawUncorrectedCount() {
        return rawUncorrectedCount;
    }

    public void setFulfilledCount(LabelledValueSet<IntegerRange, Integer> fulfilledCount) {
        this.fulfilledCount = fulfilledCount;
    }

    public LabelledValueSet<IntegerRange, Integer> getZeroedCountsTemplate() {
        return new IntegerRangeToIntegerSet(determinedCount.getLabels(), 0);
    }
}
