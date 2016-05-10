package model.implementation.populationStatistics;

import model.time.YearDate;

import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TwoDimensionDataDistribution implements DataDistibution {

    private final Map<IntegerRange, Map<IntegerRange, Double>> appliedData;
    private YearDate year;
    private String sourcePopulation;

//    private Map<IntegerRange, Map<IntegerRange, Double>> targetData;
    private String sourceOrganisation;

    public TwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, Map<IntegerRange, Double>> tableData) {
        this.year = year;
        this.sourcePopulation = sourcePopulation;
        this.sourceOrganisation = sourceOrganisation;
        this.appliedData = tableData;
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
    public int getMinRowLabelValue() {
        int min = Integer.MAX_VALUE;
        for(IntegerRange iR : appliedData.keySet()) {
            int v = iR.getMin();
            if(v < min) {
                min = v;
            }
        }
        return min;
    }

    @Override
    public IntegerRange getMaxRowLabelValue() {
        IntegerRange max = null;
        int maxV = Integer.MIN_VALUE;
        for(IntegerRange iR : appliedData.keySet()) {
            int v = iR.getMax();
            if(v > maxV) {
                max = iR;
                maxV = v;
            }
        }
        return max;

    }

    public Map<IntegerRange, Double> getData(IntegerRange row) {

        return ((Map<IntegerRange, Double>) appliedData.get(row));
    }



//    public Map<IntegerRange, Double> getData(IntegerRange row, int forNPeople) {
//
//        return null;
//    }

//    public void returnUsedData(Map<IntegerRange, Double> appliedToSimulation, int onNPeople) {
//
//
//    }


}
