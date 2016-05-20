package model.implementation.populationStatistics;

import model.time.YearDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TwoDimensionDataDistribution implements DataDistribution {

    public static Logger log = LogManager.getLogger(TwoDimensionDataDistribution.class);

    private final Map<IntegerRange, OneDimensionDataDistribution> appliedData;
    private YearDate year;
    private String sourcePopulation;

//    private Map<IntegerRange, Map<IntegerRange, Double>> targetData;
    private String sourceOrganisation;

    public TwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, OneDimensionDataDistribution> tableData) {
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

    public OneDimensionDataDistribution getData(Integer rowValue) {

        IntegerRange row = null;
        try {
            row = resolveRowValue(rowValue);
        } catch (InvalidRangeException e) {
            log.fatal(e.getMessage());
            System.exit(303);
        }

        return appliedData.get(row);
    }

    private IntegerRange resolveRowValue(Integer rowValue) {


        for(IntegerRange iR : appliedData.keySet()) {
            if(iR.contains(rowValue)) {
                return iR;
            }
        }

        throw new InvalidRangeException("RowValue does not exist in this data distribution");
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
