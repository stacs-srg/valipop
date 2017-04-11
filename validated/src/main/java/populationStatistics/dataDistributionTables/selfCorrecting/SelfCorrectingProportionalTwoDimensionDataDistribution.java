package populationStatistics.dataDistributionTables.selfCorrecting;


import dateModel.timeSteps.CompoundTimeUnit;
import populationStatistics.dataDistributionTables.DataDistribution;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import dateModel.dateImplementations.YearDate;
import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import utils.specialTypes.integerRange.IntegerRange;
import utils.specialTypes.integerRange.InvalidRangeException;


import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingProportionalTwoDimensionDataDistribution implements DataDistribution {

    // The integer range here represents the row labels (i.e. the age ranges on the ordered birth table)
    private Map<IntegerRange, SelfCorrectingOneDimensionDataDistribution> data;

    private YearDate year;
    private String sourcePopulation;

    private String sourceOrganisation;

    public SelfCorrectingProportionalTwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, SelfCorrectingOneDimensionDataDistribution> tableData) {
        this.year = year;
        this.sourceOrganisation = sourceOrganisation;
        this.sourcePopulation = sourcePopulation;
        this.data = tableData;
    }

//    @Override
//    public double getCorrectingRate(StatsKey data, CompoundTimeUnit consideredTimePeriod) {
//
//        StatsKey temp = new StatsKey(data.getXLabel(), data.getForNPeople());
//
//        return getData(data.getYLabel()).determineCount(temp, consideredTimePeriod);
//
//    }
//
//    @Override
//    public void returnAppliedRate(StatsKey data, double appliedData, CompoundTimeUnit consideredTimePeriod) {
//
//        StatsKey temp = new StatsKey(data.getXLabel(), data.getForNPeople());
//
//        getData(data.getYLabel()).returnAchievedCount(temp, appliedData, consideredTimePeriod);
//
//    }

    public SelfCorrectingOneDimensionDataDistribution getData(Integer yLabel) throws InvalidRangeException {

        IntegerRange row = resolveRowValue(yLabel);

        return data.get(row);
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
        for (IntegerRange iR : data.keySet()) {
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
        for (IntegerRange iR : data.keySet()) {
            int v = iR.getMax();
            if (v > maxV) {
                max = iR;
                maxV = v;
            }
        }
        return max;

    }

    private IntegerRange resolveRowValue(Integer rowValue) {

        for (IntegerRange iR : data.keySet()) {
            if (iR.contains(rowValue)) {
                return iR;
            }
        }

        throw new InvalidRangeException("Given value not covered by rows - value " + rowValue);
    }


    public Set<IntegerRange> getRowKeys() {
        return data.keySet();
    }
}
