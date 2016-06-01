package datastructure.summativeStatistics.structure;

import utils.time.YearDate;

import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingTwoDimensionDataDistribution extends TwoDimensionDataDistribution implements SelfCorrection {

    public SelfCorrectingTwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, OneDimensionDataDistribution> tableData) {
        super(year, sourcePopulation, sourceOrganisation, tableData);
        this.appliedData = tableData;
        this.appliedCounts = tableData;

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

        // in the case of births:
        // row value corresponds to age
        // column value corresponds to order - cV

        // get Target Rate - t
        double t = getData(data.getRowValue()).getData(data.getColumnValue());

        // get how many returned data will be applied to - L
        int L = data.getForNPeople();

        // get previous applied rate - a
        double a = getPreviousData(data.getRowValue()).getData(data.getColumnValue());

        // get number of people a has been applied to - P
        int P = getCountData(data.getRowValue()).getData(data.getColumnValue()).intValue();

        if(P == 0) {
            System.out.println("H - 0");
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
            if(data.getMaxColumnValue() - 1 < getMaxRowLabelValue().getValue()) {
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
        double a = getPreviousData(data.getRowValue()).getData(data.getColumnValue());

        // get number of people it has been applied to - P
        int P = getCountData(data.getRowValue()).getData(data.getColumnValue()).intValue();

        // get how many returned data will be applied to - L
        int L = data.getForNPeople();

        // calculate total number to be effected - T
        int T = L + P;

        // the given rate - x
        double x = appliedRate;

        // calc the new applied rate - z
        // z = (Pa + Lx) / T
        double z = (P*a + L*x) / T;

        // inc count by P
        updateCounts(data.getRowValue(), data.getColumnValue(), P);

        // update appliedData to z
        updateAppliedData(data.getRowValue(), data.getColumnValue(), z);



    }

    private void updateAppliedData(Integer rowValue, Integer columnValue, double newValue) {

        OneDimensionDataDistribution applied = appliedData.get(resolveRowValue(rowValue, appliedData));
        applied.getData().replace(applied.resolveRowValue(columnValue), newValue);


    }

    private void updateCounts(Integer row, Integer column, double newValue) {

        OneDimensionDataDistribution counts = appliedCounts.get(resolveRowValue(row, appliedCounts));
        counts.getData().replace(counts.resolveRowValue(column), newValue);

    }

    public OneDimensionDataDistribution getPreviousData(Integer rowValue) {

        IntegerRange row = null;
        try {
            row = resolveRowValue(rowValue, appliedData);
        } catch (InvalidRangeException e) {
            log.fatal(e.getMessage());
            System.exit(303);
        }

        return appliedData.get(row);
    }

    public OneDimensionDataDistribution getCountData(Integer rowValue) {

        IntegerRange row = null;
        try {
            row = resolveRowValue(rowValue, appliedCounts);
        } catch (InvalidRangeException e) {
            log.fatal(e.getMessage());
            System.exit(303);
        }

        return appliedCounts.get(row);
    }
}
