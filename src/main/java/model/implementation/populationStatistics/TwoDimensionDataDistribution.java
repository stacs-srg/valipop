package model.implementation.populationStatistics;

import model.time.TimeClock;

import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TwoDimensionDataDistribution implements DataDistibution {

    private TimeClock year;
    private String sourcePopulation;
    private String sourceOrganisation;

//    private Map<IntegerRange, Map<IntegerRange, Double>> targetData;

    private final Map<IntegerRange, Map<IntegerRange, Double>> appliedData;

    public TwoDimensionDataDistribution(TimeClock year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, Map<IntegerRange, Double>> tableData) {
        this.year = year;
        this.sourcePopulation = sourcePopulation;
        this.sourceOrganisation = sourceOrganisation;
        this.appliedData = tableData;
    }

    @Override
    public TimeClock getYear() {
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
