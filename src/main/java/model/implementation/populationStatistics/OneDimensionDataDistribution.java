package model.implementation.populationStatistics;

import model.time.YearDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OneDimensionDataDistribution implements DataDistribution {


    public static Logger log = LogManager.getLogger(OneDimensionDataDistribution.class);

    private YearDate year;
    private String sourcePopulation;
    private String sourceOrganisation;

//    private Map<IntegerRange, Double> targetData;

    private Map<IntegerRange, Double> appliedData;

    public OneDimensionDataDistribution(YearDate year,
                                        String sourcePopulation,
                                        String sourceOrganisation,
                                        Map<IntegerRange, Double> tableData) {

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

    public IntegerRange getMinRowLabelValue2() {
        int min = Integer.MAX_VALUE;
        IntegerRange label = null;
        for(IntegerRange iR : appliedData.keySet()) {
            int v = iR.getMin();
            if(v < min) {
                min = v;
                label = iR;
            }
        }
        return label;
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

    public Double getData(Integer rowValue) {

        IntegerRange row = null;
        try {
            row = resolveRowValue(rowValue);
        } catch (InvalidRangeException e) {
            log.fatal("here   " + e.getMessage());

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

        throw new InvalidRangeException("Given value not covered by rows - value " + rowValue + " | min row label " + getMinRowLabelValue2().toString() + " | max row label " + getMaxRowLabelValue().toString());
    }

    public Map<IntegerRange, Double> getData() {
        return appliedData;
    }

    public Map<IntegerRange,Double> cloneData() {
        Map<IntegerRange, Double> map = new HashMap<IntegerRange, Double>();

        for(IntegerRange iR : appliedData.keySet()) {
            map.put(iR, appliedData.get(iR));
        }

        return map;

    }

//    public Double getData(IntegerRange row, int forNPeople) {
//
//        return null;
//    }
//
//    public void returnUsedData(IntegerRange range, Double appliedToSimulation, int onNPeople) {
//
//
//    }

}
