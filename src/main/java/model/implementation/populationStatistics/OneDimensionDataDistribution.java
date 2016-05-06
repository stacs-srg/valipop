package model.implementation.populationStatistics;

import model.time.DateClock;

import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OneDimensionDataDistribution implements DataDistibution {

    private DateClock year;
    private String sourcePopulation;
    private String sourceOrganisation;

//    private Map<IntegerRange, Double> targetData;

    private Map<IntegerRange, Double> appliedData;

    public OneDimensionDataDistribution(DateClock year,
                                        String sourcePopulation,
                                        String sourceOrganisation,
                                        Map<IntegerRange, Double> tableData) {

        this.year = year;
        this.sourcePopulation = sourcePopulation;
        this.sourceOrganisation = sourceOrganisation;
        this.appliedData = tableData;
    }

    @Override
    public DateClock getYear() {
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

    public Double getData(IntegerRange row) {

        return appliedData.get(row);
    }

    public Map<IntegerRange, Double> getData() {
        return appliedData;
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
