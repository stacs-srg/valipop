package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

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
            transformedProportions.put(iR,
                    targetProportions.get(iR)
                            .productOfLabelsAndValues()
                            .reproportion()
            );
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
    public MultipleDeterminedCount determineCount(StatsKey key) {

        MultipleDeterminedCount childNumbers = distribution.determineCount(key);

        LabeledValueSet<IntegerRange, Integer> motherNumbers = childNumbers.getDeterminedCount()
                .divisionOfValuesByLabels()
                .controlledRoundingMaintainingSumProductOfLabelValues();

        return new MultipleDeterminedCount(key, motherNumbers);
    }

    @Override
    public void returnAchievedCount(DeterminedCount<LabeledValueSet<IntegerRange, Integer>> achievedCount) {

        // Transforms counts to be of children born rather than mothers giving birth
        achievedCount.setFufilledCount(achievedCount.getFufilledCount().productOfLabelsAndValues());
        distribution.returnAchievedCount(achievedCount);

    }
}
