package populationStatistics.dataDistributionTables;

import dateModel.DateUtils;
import dateModel.timeSteps.CompoundTimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dateModel.dateImplementations.YearDate;
import utils.specialTypes.integerRange.IntegerRange;
import utils.specialTypes.integerRange.InvalidRangeException;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OneDimensionDataDistribution implements DataDistribution {


    public static Logger log = LogManager.getLogger(OneDimensionDataDistribution.class);

    private final YearDate year;
    private final String sourcePopulation;
    private final String sourceOrganisation;

    protected final Map<IntegerRange, Double> targetData;

    public OneDimensionDataDistribution(YearDate year,
                                        String sourcePopulation,
                                        String sourceOrganisation,
                                        Map<IntegerRange, Double> tableData) {

        this.year = year;
        this.sourcePopulation = sourcePopulation;
        this.sourceOrganisation = sourceOrganisation;
        this.targetData = tableData;
    }

    public void updateValue(Integer row, double newValue) {

        IntegerRange rowRange = resolveRowValue(row);

        targetData.replace(rowRange, newValue);
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

    public IntegerRange getMinRowLabelValue2() {
        int min = Integer.MAX_VALUE;
        IntegerRange label = null;
        for (IntegerRange iR : targetData.keySet()) {
            int v = iR.getMin();
            if (v < min) {
                min = v;
                label = iR;
            }
        }
        return label;
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

    public double getRate(Integer rowValue) throws InvalidRangeException {

        IntegerRange row = resolveRowValue(rowValue);

        return targetData.get(row);
    }

    public double getRate(Integer rowValue, CompoundTimeUnit timeStep) {

        double basicRate = getRate(rowValue);

        double stepsInYear = DateUtils.stepsInYear(timeStep);
        double adjustedRate = 1 - Math.pow(1 - basicRate, 1 / stepsInYear);

        return adjustedRate;
    }

    public IntegerRange resolveRowValue(Integer rowValue) {

        for (IntegerRange iR : targetData.keySet()) {
            if (iR.contains(rowValue)) {
                return iR;
            }
        }

        throw new InvalidRangeException("Given value not covered by rows - value " + rowValue);
    }

    public Map<IntegerRange, Double> getRate() {
        return targetData;
    }

    public Map<IntegerRange, Double> cloneData() {
        Map<IntegerRange, Double> map = new HashMap<IntegerRange, Double>();

        for (IntegerRange iR : targetData.keySet()) {
            map.put(iR, targetData.get(iR));
        }

        return map;

    }

    public OneDimensionDataDistribution clone() {

        return new OneDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, cloneData());

    }

    public void print(PrintStream out) {

        IntegerRange[] orderedKeys = getRate().keySet().toArray(new IntegerRange[getRate().keySet().size()]);
        Arrays.sort(orderedKeys, IntegerRange::compareTo);

        out.println("YEAR\t" + year);
        out.println("POPULATION\t" + sourcePopulation);
        out.println("SOURCE\t" + sourceOrganisation);
        out.println("DATA");

        for(IntegerRange iR : orderedKeys) {
            out.println(iR.getValue() + "\t" + targetData.get(iR));
        }

        out.println();

    }

}
