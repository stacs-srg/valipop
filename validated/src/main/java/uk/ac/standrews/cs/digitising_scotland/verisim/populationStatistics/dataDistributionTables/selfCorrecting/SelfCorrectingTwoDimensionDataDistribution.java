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


import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.DataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.InvalidRangeException;


import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingTwoDimensionDataDistribution implements DataDistribution {

    // The integer range here represents the row labels (i.e. the age ranges on the ordered birth table)
    private Map<IntegerRange, SelfCorrectingOneDimensionDataDistribution> data;

    private YearDate year;
    private String sourcePopulation;

    private String sourceOrganisation;

    public SelfCorrectingTwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, SelfCorrectingOneDimensionDataDistribution> tableData) {
        this.year = year;
        this.sourceOrganisation = sourceOrganisation;
        this.sourcePopulation = sourcePopulation;
        this.data = tableData;
    }

    private static final double FACTOR = 1.007462401 ;

    public SingleDeterminedCount determineCount(StatsKey key) {
        try {
            SingleDeterminedCount sDC = getData(key.getXLabel()).determineCount(key);
            int adjCount = Integer.parseInt(String.valueOf(Math.round(sDC.getDeterminedCount() * FACTOR)));
            return new SingleDeterminedCount(sDC.getKey(), adjCount, sDC.getRawCorrectedCount(), sDC. getRawUncorrectedCount());
        } catch (InvalidRangeException e) {
            return new SingleDeterminedCount(key, 0, 0, 0);
        }

    }

    public void returnAchievedCount(DeterminedCount<Integer, Double> achievedCount) {
        try {
            int adjCount = Integer.parseInt(String.valueOf(Math.round(achievedCount.getFufilledCount() / FACTOR)));
            achievedCount.setFufilledCount(adjCount);
            getData(achievedCount.getKey().getXLabel()).returnAchievedCount(achievedCount);
        } catch (InvalidRangeException e) {
            if(achievedCount.getDeterminedCount() == 0) {
                // all okay, a blank DeterminedCount had been issued due as no recorded data on the request
            } else {
                // Something is not right here
                throw e;
            }
        }

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

    @Override
    public Collection<IntegerRange> getLabels() {
        return getRowLabels();
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
//        outputMap(targetRates, true, resultsOutput);
//        resultsOutput.println("APPLIED");
//        outputMap(appliedData, true, resultsOutput);
//        resultsOutput.println("DELTAS");
//        printDeltas(appliedData, targetRates, resultsOutput);
//        resultsOutput.println("COUNTS");
//        outputMap(appliedCounts, false, resultsOutput);
//    }

    private void printDeltas(Map<IntegerRange, OneDimensionDataDistribution> appliedData, Map<IntegerRange, OneDimensionDataDistribution> targetData, PrintStream resultsOutput) {

        IntegerRange[] keys = targetData.keySet().toArray(new IntegerRange[targetData.keySet().size()]);
        Arrays.sort(keys, IntegerRange::compareTo);

        for (IntegerRange iR : keys) {
            resultsOutput.print(iR.toString() + " | ");

            Map<IntegerRange, Double> targetRow = targetData.get(iR).getRate();
            Map<IntegerRange, Double> appliedRow = appliedData.get(iR).getRate();

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

            Map<IntegerRange, Double> row = data.get(iR).getRate();
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



    public Set<IntegerRange> getRowLabels() {
        return data.keySet();
    }

    public Set<IntegerRange> getColumnLabels () {
        return data.get(resolveRowValue(getSmallestLabel())).getLabels();
    }
}
