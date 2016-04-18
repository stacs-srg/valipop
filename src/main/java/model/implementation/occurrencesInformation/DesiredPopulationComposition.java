package model.implementation.occurrencesInformation;

import model.enums.EventType;
import model.enums.Gender;
import model.implementation.analysis.PopulationComposition;
import model.interfaces.dataStores.PopulationDateRange;
import model.interfaces.dataStores.informationAccess.EventRateTables;
import model.interfaces.dataStores.informationAccess.StatisticalTables;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;
import model.interfaces.dataStores.informationPassing.tableTypes.TwoWayTable;

/**
 * The DesiredPopulationComposition holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DesiredPopulationComposition implements PopulationComposition, EventRateTables {


    @Override
    public int getEarliestDay() {
        return 0;
    }

    @Override
    public int getLatestDay() {
        return 0;
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
    public OneWayTable<Double> getBirthRates(int year) {
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
