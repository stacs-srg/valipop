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
package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting;

import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ProportionalDistribution;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.StatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToDoubleSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.OperableLabelledValueSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MotherChildAdapter implements ProportionalDistribution {

    private SelfCorrectingProportionalDistribution distribution;

    public MotherChildAdapter(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, LabelledValueSet<IntegerRange, Double>> targetProportions) {

        Map<IntegerRange, LabelledValueSet<IntegerRange, Double>> transformedProportions = new HashMap<>();

        for(Map.Entry<IntegerRange, LabelledValueSet<IntegerRange, Double>> iR : targetProportions.entrySet()) {
            LabelledValueSet<IntegerRange, Double> tp = iR.getValue();

            if(tp.getSumOfValues() != 0) {
                transformedProportions.put(iR.getKey(),
                        new IntegerRangeToDoubleSet(tp)
                            .productOfLabelsAndValues()
                            .reproportion()
                );
            } else {
                transformedProportions.put(iR.getKey(), tp);
            }
        }

        distribution = new SelfCorrectingProportionalDistribution(year, sourcePopulation, sourceOrganisation, transformedProportions);

    }

    @Override
    public YearDate getYear() {
        return distribution.getYear();
    }

    @Override
    public String getSourcePopulation() {
        return distribution.getSourcePopulation();
    }

    @Override
    public String getSourceOrganisation() {
        return distribution.getSourceOrganisation();
    }

    @Override
    public IntegerRange getSmallestLabel() {
        return distribution.getSmallestLabel();
    }

    @Override
    public IntegerRange getLargestLabel() {
        return distribution.getLargestLabel();
    }

    @Override
    public Collection<IntegerRange> getLabels() {
        return distribution.getLabels();
    }

    @Override
    public MultipleDeterminedCount determineCount(StatsKey key, Config config) {

        MultipleDeterminedCount childNumbers = distribution.determineCount(key, config);

        LabelledValueSet<IntegerRange, Double> rawCorrectedMotherNumbers =
                new IntegerRangeToDoubleSet(childNumbers.getRawCorrectedCount())
                    .divisionOfValuesByLabels();

        LabelledValueSet<IntegerRange, Double> rawUncorrectedMotherNumbers =
                new IntegerRangeToDoubleSet(childNumbers.getRawUncorrectedCount())
                    .divisionOfValuesByLabels();

        LabelledValueSet<IntegerRange, Integer> motherNumbers;

        try {
            motherNumbers = new IntegerRangeToIntegerSet(childNumbers.getDeterminedCount())
                    .divisionOfValuesByLabels().controlledRoundingMaintainingSumProductOfLabelValues();

        } catch (NullPointerException e) {
            return new MultipleDeterminedCount(key, null, rawCorrectedMotherNumbers, rawUncorrectedMotherNumbers);
        }

        return new MultipleDeterminedCount(key, motherNumbers, rawCorrectedMotherNumbers, rawUncorrectedMotherNumbers);
    }

    @Override
    public void returnAchievedCount(DeterminedCount<LabelledValueSet<IntegerRange, Integer>, LabelledValueSet<IntegerRange, Double>> achievedCount) {

        // Transforms counts to be of children born rather than mothers giving birth
        achievedCount.setFufilledCount(new IntegerRangeToIntegerSet(achievedCount.getFufilledCount())
                                                                                        .productOfLabelsAndValues());
        distribution.returnAchievedCount(achievedCount);

    }
}
