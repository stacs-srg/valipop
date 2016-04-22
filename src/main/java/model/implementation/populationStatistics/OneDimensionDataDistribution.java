package model.implementation.populationStatistics;

import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OneDimensionDataDistribution implements DataDistibution {

    private int year;
    private String sourcePopulation;
    private String sourceOrganisation;

    private Map<IntegerRange, Double> targetData;
    private Map<IntegerRange, Double> appliedData;

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

    public Double getData(IntegerRange row, int forNPeople) {

        return null;
    }

    public void returnUsedData(IntegerRange range, Double appliedToSimulation, int onNPeople) {


    }

}
