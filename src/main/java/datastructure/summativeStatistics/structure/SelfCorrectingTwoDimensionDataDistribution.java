package datastructure.summativeStatistics.structure;

import utils.MapUtils;
import utils.time.YearDate;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingTwoDimensionDataDistribution extends TwoDimensionDataDistribution implements SelfCorrection {

    private Map<IntegerRange, OneDimensionDataDistribution> appliedData;
    private Map<IntegerRange, OneDimensionDataDistribution> appliedCounts;
    private int maxCol = 10;

    public SelfCorrectingTwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, OneDimensionDataDistribution> tableData) {
        super(year, sourcePopulation, sourceOrganisation, tableData);
        this.appliedData = MapUtils.clone(tableData);
        this.appliedCounts = MapUtils.clone(tableData);

        for (IntegerRange iR : appliedCounts.keySet()) {
            OneDimensionDataDistribution t = appliedCounts.get(iR);
            for (IntegerRange iR2 : t.getData().keySet()) {
                t.getData().replace(iR2, 0.0);
            }
        }

    }

    @Override
    public double getCorrectingData(DataKey data) {

        if (maxCol <= data.getColumnValue()) {
            return 0.0;
        }

        // in the case of births:
        // row value corresponds to age
        // column value corresponds to order - cV

        // get Target Rate - t
        double t = getData(data.getRowValue()).getData(data.getColumnValue());

        // get how many returned data will be applied to - L
        int L = data.getForNPeople();

        // get previous applied rate - a
        double a = getAppliedRates(data.getRowValue()).getData(data.getColumnValue());

        // get number of people a has been applied to - P
        Double dP = getCounts(data.getRowValue()).getData(data.getColumnValue());

        int P = dP.intValue();

        if (P == 0) {
            return t;
        }

        // calculate total number to be effected - T
        int T = L + P;

        // calculate rate to achieve correction - x
        // x = (Tt - Pa) / L
        double x = (T * t - P * a) / L;


        int maxBirthOrderInTargetTable = targetData.get(targetData.keySet().iterator().next()).getLargestLabel().getValue();

        // if ! columnValue >= getLargestLabel()
//        System.out.println("MRV = " + targetData.get(targetData.keySet().iterator().next()).getLargestLabel().toString());
        if (!(data.getColumnValue() >= maxBirthOrderInTargetTable)) {
            return x;
        } else {
            // scale to appropriate value
            // if mcv - 1 > mrl
            if (data.getMaxColumnValue() - 1 > maxBirthOrderInTargetTable) {

                // r = x(cV - mcv - 1)^2 / (mcv - mrl)^2
//                double temp = (x * Math.pow(data.getColumnValue() - data.getMaxColumnValue() - 1, 2)) / Math.pow(data.getMaxColumnValue() - maxBirthOrderInTargetTable, 2);
                double temp = (2 * x * (data.getMaxColumnValue() + 1 - data.getColumnValue())) / (double) (data.getMaxColumnValue() + 1 + maxBirthOrderInTargetTable);

//                System.out.printf("%.3f | " + data.getColumnValue() + " | " + temp + "\n", x);

                return temp;
            } else {
                // if mcv == mrl
                if (data.getMaxColumnValue() == maxBirthOrderInTargetTable) {
                    // r = 2x
                    return 2 * x;
                } else {
                    // r = x/2
                    return x / 2;
                }
            }
        }
    }

    @Override
    public void returnAppliedData(DataKey data, double appliedRate) {


        // get previous applied rate - a
        double a = getAppliedRates(data.getRowValue()).getData(data.getColumnValue());

        // get number of people it has been applied to - P
        int P = getCounts(data.getRowValue()).getData(data.getColumnValue()).intValue();

        // get how many returned data will be applied to - L
        int L = data.getForNPeople();

        // calculate total number to be effected - T
        int T = L + P;

        // the given rate - x

//        double temp = (x * Math.pow(data.getColumnValue() - data.getMaxColumnValue() - 1, 2)) / Math.pow(data.getMaxColumnValue() - maxBirthOrderInTargetTable, 2);

        int maxBirthOrderInTargetTable = targetData.get(targetData.keySet().iterator().next()).getLargestLabel().getValue();

        double x;
        if (data.getMaxColumnValue() - 1 > maxBirthOrderInTargetTable) {
//            x = (appliedRate * Math.pow(data.getMaxColumnValue() - maxBirthOrderInTargetTable, 2)) / Math.pow(data.getColumnValue() - data.getMaxColumnValue() - 1, 2);
            x = (appliedRate * (data.getMaxColumnValue() + 1 + maxBirthOrderInTargetTable)) / (2 * (data.getMaxColumnValue() + 1 - data.getColumnValue()));
        } else {
            x = appliedRate;
        }

        // calc the new applied rate - z
        // z = (Pa + Lx) / T
        double z = (P * a + L * x) / T;

        // inc count by T
        updateCounts(data.getRowValue(), data.getColumnValue(), T);

        // update appliedData to z
        if (Double.isNaN(z)) {
            System.out.println(T + " " + P + " " + a + " " + L + " " + x);
        }
        updateAppliedRates(data.getRowValue(), data.getColumnValue(), z);


    }

    private void updateAppliedRates(Integer rowValue, Integer columnValue, double newValue) {

        appliedData.get(resolveRowValue(rowValue, appliedData)).updateValue(columnValue, newValue);

    }

    private void updateCounts(Integer row, Integer column, double newValue) {

        OneDimensionDataDistribution counts = appliedCounts.get(resolveRowValue(row, appliedCounts));
        counts.getData().replace(counts.resolveRowValue(column), newValue);

    }

    public OneDimensionDataDistribution getAppliedRates(Integer rowValue) {

        IntegerRange row = null;
        try {
            row = resolveRowValue(rowValue, appliedData);
        } catch (InvalidRangeException e) {
            log.fatal(e.getMessage());
            System.exit(303);
        }

        return appliedData.get(row);
    }

    public OneDimensionDataDistribution getCounts(Integer rowValue) {

        IntegerRange row = null;
        try {
            row = resolveRowValue(rowValue, appliedCounts);
        } catch (InvalidRangeException e) {
            log.fatal(e.getMessage());
            System.exit(303);
        }

        return appliedCounts.get(row);
    }

    public void outputResults(PrintStream resultsOutput) {
        resultsOutput.println("TARGET");
        outputMap(targetData, true, resultsOutput);
        resultsOutput.println("APPLIED");
        outputMap(appliedData, true, resultsOutput);
        resultsOutput.println("DELTAS");
        printDeltas(appliedData, targetData, resultsOutput);
        resultsOutput.println("COUNTS");
        outputMap(appliedCounts, false, resultsOutput);
    }

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

        IntegerRange[] keys = data.keySet().toArray(new IntegerRange[targetData.keySet().size()]);
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
}
