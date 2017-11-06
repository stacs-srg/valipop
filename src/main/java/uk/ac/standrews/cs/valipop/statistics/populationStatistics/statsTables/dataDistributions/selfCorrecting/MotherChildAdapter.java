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
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabeledValueSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MotherChildAdapter implements ProportionalDistribution {

    private SelfCorrectingProportionalDistribution distribution;

    public MotherChildAdapter(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, LabeledValueSet<IntegerRange, Double>> targetProportions) {

        Map<IntegerRange, LabeledValueSet<IntegerRange, Double>> transformedProportions = new HashMap<>();

        for(Map.Entry<IntegerRange, LabeledValueSet<IntegerRange, Double>> iR : targetProportions.entrySet()) {
            LabeledValueSet<IntegerRange, Double> tp = iR.getValue();

            if(tp.getSumOfValues() != 0) {
                transformedProportions.put(iR.getKey(),
                        tp
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
    public int getSmallestLabel() {
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

        LabeledValueSet<IntegerRange, Double> rawCorrectedMotherNumbers = childNumbers.getRawCorrectedCount()
                .divisionOfValuesByLabels();

        LabeledValueSet<IntegerRange, Double> rawUncorrectedMotherNumbers = childNumbers.getRawUncorrectedCount()
                .divisionOfValuesByLabels();

        LabeledValueSet<IntegerRange, Integer> motherNumbers;

        try {
            motherNumbers = childNumbers.getDeterminedCount()
                    .divisionOfValuesByLabels()
                    .controlledRoundingMaintainingSumProductOfLabelValues();

        } catch (NullPointerException e) {
            return new MultipleDeterminedCount(key, null, rawCorrectedMotherNumbers, rawUncorrectedMotherNumbers);
        }

        return new MultipleDeterminedCount(key, motherNumbers, rawCorrectedMotherNumbers, rawUncorrectedMotherNumbers);
    }

    @Override
    public void returnAchievedCount(DeterminedCount<LabeledValueSet<IntegerRange, Integer>, LabeledValueSet<IntegerRange, Double>> achievedCount) {

        // Transforms counts to be of children born rather than mothers giving birth
        achievedCount.setFufilledCount(achievedCount.getFufilledCount().productOfLabelsAndValues());
        distribution.returnAchievedCount(achievedCount);

    }
}
