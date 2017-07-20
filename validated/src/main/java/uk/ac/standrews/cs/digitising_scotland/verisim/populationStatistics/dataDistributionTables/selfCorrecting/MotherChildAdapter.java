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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MotherChildAdapter implements ProportionalDistributionAdapter {

    private SelfCorrectingProportionalDistribution distribution;

    public MotherChildAdapter(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, LabeledValueSet<IntegerRange, Double>> targetProportions) {

        Map<IntegerRange, LabeledValueSet<IntegerRange, Double>> transformedProportions = new HashMap<>();

        for(IntegerRange iR : targetProportions.keySet()) {
            LabeledValueSet<IntegerRange, Double> tp = targetProportions.get(iR);

            if(tp.getSumOfValues() != 0) {
                transformedProportions.put(iR,
                        tp
                        .productOfLabelsAndValues()
                        .reproportion()
                );
            } else {
                transformedProportions.put(iR, tp);
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
    public MultipleDeterminedCount determineCount(StatsKey key) {

        MultipleDeterminedCount childNumbers = distribution.determineCount(key);

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
