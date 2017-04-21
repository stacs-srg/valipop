package populationStatistics.dataDistributionTables.selfCorrecting;


import dateModel.timeSteps.CompoundTimeUnit;
import populationStatistics.dataDistributionTables.DataDistribution;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import dateModel.dateImplementations.YearDate;
import populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import utils.specialTypes.IntegerRangeToIntegerSet;
import utils.specialTypes.LabeledValueSet;
import utils.specialTypes.integerRange.IntegerRange;
import utils.specialTypes.integerRange.InvalidRangeException;


import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingProportionalTwoDimensionDataDistribution implements DataDistribution {

    // The integer range here represents the row labels (i.e. the age ranges on the ordered birth table)
    private Map<IntegerRange, LabeledValueSet<IntegerRange, Double>> targetProportions;
    private Map<IntegerRange, LabeledValueSet<IntegerRange, Integer>> achievedCounts;

    private YearDate year;
    private String sourcePopulation;
    private String sourceOrganisation;

    public SelfCorrectingProportionalTwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, LabeledValueSet<IntegerRange, Double>> targetProportions) {
        this.year = year;
        this.sourceOrganisation = sourceOrganisation;
        this.sourcePopulation = sourcePopulation;
        this.targetProportions = targetProportions;

        this.achievedCounts = new HashMap<>();

        for(IntegerRange iR : targetProportions.keySet()) {
            achievedCounts.put(iR, new IntegerRangeToIntegerSet(targetProportions.get(iR).getLabels(), 0));
        }
    }

    public MultipleDeterminedCount determineCount(StatsKey key) {

        int age = key.getYLabel();

        LabeledValueSet<IntegerRange, Integer> achievedCountsForAge = achievedCounts.get(resolveRowValue(age));
        Integer sumOfAC = achievedCountsForAge.getSumOfValues();
        Integer totalCount = sumOfAC + key.getForNPeople();

        // Verbose code down to end of method - commented line is one line solution
        LabeledValueSet<IntegerRange, Double> targetProportionsForAge = targetProportions.get(resolveRowValue(age));
        LabeledValueSet<IntegerRange, Double> targetNumbers = targetProportionsForAge.productOfValuesAndN(totalCount);

        LabeledValueSet<IntegerRange, Double> targetNumbersMinusAchievedCounts =
                targetNumbers.valuesSubtractValues(achievedCountsForAge);

        LabeledValueSet<IntegerRange, Integer> retValues =
                targetNumbersMinusAchievedCounts.controlledRoundingMaintainingSum();

        //        LabeledValueSet<IntegerRange, Integer> retValues = targetProportions.get(resolveRowValue(age)).productOfValuesAndN(totalCount).valuesSubtractValues(achievedCountsForAge).controlledRoundingMaintainingSum();

        return new MultipleDeterminedCount(key, retValues);
    }

    public void returnAchievedCount(MultipleDeterminedCount achievedCount) {

        int age = achievedCount.getKey().getYLabel();
        LabeledValueSet<IntegerRange, Integer> previousAchievedCountsForAge = achievedCounts.get(resolveRowValue(age));
        LabeledValueSet<IntegerRange, Integer> newAchievedCountsForAge = achievedCount.getFufilledCount();

        LabeledValueSet<IntegerRange, Integer> summedAchievedCountsForAge = previousAchievedCountsForAge.valuesPlusValues(newAchievedCountsForAge);
        achievedCounts.replace(resolveRowValue(age), previousAchievedCountsForAge, summedAchievedCountsForAge);

    }


    @Override
    public YearDate getYear() {
        return year;
    }

    @Override
    public String getSourcePopulation() {
        return sourcePopulation;
    }

    @Override
    public String getSourceOrganisation() {
        return sourceOrganisation;
    }

    @Override
    public int getSmallestLabel() {
        int min = Integer.MAX_VALUE;
        for (IntegerRange iR : targetProportions.keySet()) {
            int v = iR.getMin();
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

    @Override
    public IntegerRange getLargestLabel() {
        IntegerRange max = null;
        int maxV = Integer.MIN_VALUE;
        for (IntegerRange iR : targetProportions.keySet()) {
            int v = iR.getMax();
            if (v > maxV) {
                max = iR;
                maxV = v;
            }
        }
        return max;

    }

    private IntegerRange resolveRowValue(Integer rowValue) {

        for (IntegerRange iR : targetProportions.keySet()) {
            if (iR.contains(rowValue)) {
                return iR;
            }
        }

        throw new InvalidRangeException("Given value not covered by rows - value " + rowValue);
    }

}
