package model.implementation.populationStatistics;

import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TwoDimensionDataDistribution implements DataDistibution {

    private int year;
    private String sourcePopulation;
    private String sourceOrganisation;

    private Map<IntegerRange, Map<IntegerRange, Double>> targetData;
    private Map<IntegerRange, Map<IntegerRange, Double>> appliedData;

    @Override
    public int getYear() {
        return 0;
    }

    @Override
    public String getSourcePopulation() {
        return null;
    }

    @Override
    public String getSourceOrganisation() {
        return null;
    }

    public Map<IntegerRange, Double> getData(IntegerRange row, int forNPeople) {

        return null;
    }

    public void returnUsedData(Map<IntegerRange, Double> appliedToSimulation, int onNPeople) {


    }


}
