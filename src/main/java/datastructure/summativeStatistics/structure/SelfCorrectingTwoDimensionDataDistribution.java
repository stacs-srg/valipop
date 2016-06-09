package datastructure.summativeStatistics.structure;

import utils.MapUtils;
import utils.time.YearDate;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingTwoDimensionDataDistribution extends TwoDimensionDataDistribution implements SelfCorrection {

    public SelfCorrectingTwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, OneDimensionDataDistribution> tableData) {
        super(year, sourcePopulation, sourceOrganisation, tableData);
        this.appliedData = MapUtils.clone(tableData);
        this.appliedCounts = MapUtils.clone(tableData);

        for(IntegerRange iR : appliedCounts.keySet()) {
            OneDimensionDataDistribution t = appliedCounts.get(iR);
            for(IntegerRange iR2 : t.getData().keySet()) {
                t.getData().replace(iR2, 0.0);
            }
        }

    }

    private Map<IntegerRange, OneDimensionDataDistribution> appliedData;
    private Map<IntegerRange, OneDimensionDataDistribution> appliedCounts;


    @Override
    public double getCorrectingData(DataKey data) {

        // TODO this is likely/is broke

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

        if(P == 0) {
            return t;
        }

        // calculate total number to be effected - T
        int T = L + P;

        // calculate rate to achieve correction - x
        // x = (Tt - Pa) / L
        double x = (T*t - P*a) / L;

        // if ! columnValue >= getMaxRowLabelValue()
        if(!(data.getColumnValue() >= getMaxRowLabelValue().getValue())) {
            // return x
            return x;
        } else {
            // scale to appropriate value
            // if mcv - 1 > mrl
            if(data.getMaxColumnValue() - 1 > getMaxRowLabelValue().getValue()) {
                // r = x(cV - mcv - 1)^2 / (mcv - mrl)^2
                return x * Math.pow(data.getColumnValue() - data.getMaxColumnValue() - 1, 2) / Math.pow(data.getMaxColumnValue() - getMaxRowLabelValue().getValue(), 2);
            } else {
                // if mcv == mrl
                if(data.getMaxColumnValue() == getMaxRowLabelValue().getValue()) {
                    // r = 2x
                    return 2*x;
                } else {
                    // r = x/2
                    return x/2;
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
        double x = appliedRate;

        // calc the new applied rate - z
        // z = (Pa + Lx) / T
        double z = (P*a + L*x) / T;

        // inc count by T
        updateCounts(data.getRowValue(), data.getColumnValue(), T);

        // update appliedData to z
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

    public void print() {
        System.out.println("TARGET");
        printMap(targetData);
        System.out.println("APPLIED");
        printMap(appliedData);
        System.out.println("COUNTS");
        printMap(appliedCounts);
    }

    public void printMap(Map<IntegerRange, OneDimensionDataDistribution> data) {

        IntegerRange[] keys = data.keySet().toArray(new IntegerRange[targetData.keySet().size()]);
        Arrays.sort(keys, IntegerRange::compareTo);

        for(IntegerRange iR : keys) {
            System.out.print(iR.toString() + " | ");

            Map<IntegerRange, Double> row = data.get(iR).getData();
            IntegerRange[] orderedKeys = row.keySet().toArray(new IntegerRange[row.keySet().size()]);
            Arrays.sort(orderedKeys, IntegerRange::compareTo);

            for(IntegerRange iR2 : orderedKeys) {
                System.out.print(row.get(iR2) + " | ");
            }

            System.out.println();

        }


        System.out.println();

    }
}
