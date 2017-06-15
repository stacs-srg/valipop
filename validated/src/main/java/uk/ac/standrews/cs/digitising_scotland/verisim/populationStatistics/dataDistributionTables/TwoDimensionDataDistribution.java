package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.InvalidRangeException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TwoDimensionDataDistribution implements DataDistribution {

    public static Logger log = LogManager.getLogger(TwoDimensionDataDistribution.class);

    protected final Map<IntegerRange, OneDimensionDataDistribution> targetData;
    private YearDate year;
    private String sourcePopulation;

    private String sourceOrganisation;

    public TwoDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, OneDimensionDataDistribution> tableData) {
        this.year = year;
        this.sourcePopulation = sourcePopulation;
        this.sourceOrganisation = sourceOrganisation;
        this.targetData = tableData;
    }

    protected static IntegerRange resolveRowValue(Integer rowValue, Map<IntegerRange, OneDimensionDataDistribution> data) {


        for (IntegerRange iR : data.keySet()) {
            if (iR.contains(rowValue)) {
                return iR;
            }
        }

        throw new InvalidRangeException("RowValue does not exist in this data distribution");
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
        for (IntegerRange iR : targetData.keySet()) {
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
        for (IntegerRange iR : targetData.keySet()) {
            int v = iR.getMax();
            if (v > maxV) {
                max = iR;
                maxV = v;
            }
        }
        return max;

    }

    public OneDimensionDataDistribution getData(Integer rowValue) throws InvalidRangeException {

        IntegerRange row = resolveRowValue(rowValue, targetData);


        return targetData.get(row);
    }

    public Map<IntegerRange, OneDimensionDataDistribution> cloneData() {

        Map<IntegerRange, OneDimensionDataDistribution> clone = new HashMap<IntegerRange, OneDimensionDataDistribution>();

        for (IntegerRange iR : targetData.keySet()) {
            OneDimensionDataDistribution d = targetData.get(iR);
            clone.put(iR, new OneDimensionDataDistribution(d.getYear(), d.getSourcePopulation(), d.getSourceOrganisation(), d.cloneData()));
        }

        return clone;

    }


//    public Map<IntegerRange, Double> getRate(IntegerRange row, int forNPeople) {
//
//        return null;
//    }

//    public void returnUsedData(Map<IntegerRange, Double> appliedToSimulation, int onNPeople) {
//
//
//    }


}
