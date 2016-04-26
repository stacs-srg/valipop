package model.implementation.populationStatistics;

import model.enums.EventType;
import model.enums.Gender;
import model.implementation.analysis.PopulationComposition;
import model.implementation.config.Config;
import model.interfaces.dataStores.informationAccess.EventRateTables;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;
import model.interfaces.dataStores.informationPassing.tableTypes.TwoWayTable;
import model.interfaces.populationModel.Population;
import model.time.TimeClock;

import java.util.Map;

/**
 * The PopulationStatistics holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatistics implements PopulationComposition, EventRateTables {

    TimeClock startDay;
    TimeClock endDay;

    Map<TimeClock, OneDimensionDataDistribution> death;
    Map<TimeClock, TwoDimensionDataDistribution> partnering;
    Map<TimeClock, TwoDimensionDataDistribution> orderedBirth;
    Map<TimeClock, TwoDimensionDataDistribution> multipleBirth;
    Map<TimeClock, OneDimensionDataDistribution> separation;

    public PopulationStatistics(Config config,
                                Map<TimeClock, OneDimensionDataDistribution> death,
                                Map<TimeClock, TwoDimensionDataDistribution> partnering,
                                Map<TimeClock, TwoDimensionDataDistribution> orderedBirth,
                                Map<TimeClock, TwoDimensionDataDistribution> multipleBirth,
                                Map<TimeClock, OneDimensionDataDistribution> separation) {

        this.death = death;
        this.partnering = partnering;
        this.orderedBirth = orderedBirth;
        this.multipleBirth = multipleBirth;
        this.separation = separation;

        this.startDay = config.gettS();
        this.endDay = config.gettE();


    }

    @Override
    public TimeClock getEarliestDay() {
        return null;
    }

    @Override
    public TimeClock getLatestDay() {
        return null;
    }


    @Override
    public OneWayTable<Double> getDeathRates(int year, Gender gender) {
        return null;
    }

    @Override
    public TwoWayTable<Double> getPartneringRates(int year) {
        return null;
    }

    @Override
    public TwoWayTable<Double> getOrderedBirthRates(int year) {
        return null;
    }

    @Override
    public TwoWayTable<Double> getMultipleBirthRates(int year) {
        return null;
    }

    @Override
    public OneWayTable<Double> getSeparationByChildCountRates(int year) {
        return null;
    }

    @Override
    public OneWayTable<Integer> getSurvivorTable(int startYear, int timePeriod, EventType event) {
        return null;
    }
}
