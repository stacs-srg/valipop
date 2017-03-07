package populationStatistics.dataDistributionTables.selfCorrecting;


import populationStatistics.dataDistributionTables.DataDistribution;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import dateModel.dateImplementations.YearDate;
import utils.specialTypes.dataKeys.DataKey;
import utils.specialTypes.integerRange.IntegerRange;
import utils.specialTypes.integerRange.InvalidRangeException;


import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingTwoDimensionDataDistribution implements DataDistribution, SelfCorrection {

    // The integer range here represents the row labels (i.e. the age ranges on the ordered birth table)
    private Map<IntegerRange, SelfCorrectingOneDimensionDataDistribution> data;

    private int maxCol = 10;
    private YearDate year;
    private String sourcePopulation;

    private String sourceOrganisation;

    public SelfCorrectingTwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, SelfCorrectingOneDimensionDataDistribution> tableData) {
        this.year = year;
        this.sourceOrganisation = sourceOrganisation;
        this.sourcePopulation = sourcePopulation;
        this.data = tableData;
    }

    @Override
    public double getCorrectingRate(DataKey data) {

        DataKey temp = new DataKey(data.getXLabel(), data.getForNPeople());

        return getData(data.getYLabel()).getCorrectingRate(temp);

    }

    @Override
    public void returnAppliedRate(DataKey data, double appliedData) {

        DataKey temp = new DataKey(data.getXLabel(), data.getForNPeople());

        getData(data.getYLabel()).returnAppliedRate(temp, appliedData);

    }

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

//    public void outputResults(PrintStream resultsOutput) {
//        resultsOutput.println("TARGET");
//        outputMap(targetData, true, resultsOutput);
//        resultsOutput.println("APPLIED");
//        outputMap(appliedData, true, resultsOutput);
//        resultsOutput.println("DELTAS");
//        printDeltas(appliedData, targetData, resultsOutput);
//        resultsOutput.println("COUNTS");
//        outputMap(appliedCounts, false, resultsOutput);
//    }

    private void printDeltas(Map<IntegerRange, OneDimensionDataDistribution> appliedData, Map<IntegerRange, OneDimensionDataDistribution> targetData, PrintStream resultsOutput) {

        IntegerRange[] keys = targetData.keySet().toArray(new IntegerRange[targetData.keySet().size()]);
        Arrays.sort(keys, IntegerRange::compareTo);

        for (IntegerRange iR : keys) {
            resultsOutput.print(iR.toString() + " | ");

            Map<IntegerRange, Double> targetRow = targetData.get(iR).getData();
            Map<IntegerRange, Double> appliedRow = appliedData.get(iR).getData();

            IntegerRange[] orderedKeys = targetRow.keySet().toArray(new IntegerRange[targetRow.keySet().size()]);
            Arrays.sort(orderedKeys, IntegerRange::compareTo);

            for (IntegerRange iR2 : orderedKeys) {
                resultsOutput.printf("%+.4f | ", appliedRow.get(iR2) - targetRow.get(iR2));
            }

            resultsOutput.println();

        }

        resultsOutput.println();

    }

    public void outputMap(Map<IntegerRange, OneDimensionDataDistribution> data, boolean decimal, PrintStream resultsOutput) {

        IntegerRange[] keys = data.keySet().toArray(new IntegerRange[data.keySet().size()]);
        Arrays.sort(keys, IntegerRange::compareTo);

        for (IntegerRange iR : keys) {
            resultsOutput.print(iR.toString() + " | ");

            Map<IntegerRange, Double> row = data.get(iR).getData();
            IntegerRange[] orderedKeys = row.keySet().toArray(new IntegerRange[row.keySet().size()]);
            Arrays.sort(orderedKeys, IntegerRange::compareTo);

            for (IntegerRange iR2 : orderedKeys) {
                if (decimal) {
                    resultsOutput.printf("%.4f | ", row.get(iR2));
                } else {
                    resultsOutput.printf("%.0f | ", row.get(iR2));
                }
            }

            resultsOutput.println();

        }


        resultsOutput.println();

    }


    public Set<IntegerRange> getRowKeys() {
        return data.keySet();
    }
}
