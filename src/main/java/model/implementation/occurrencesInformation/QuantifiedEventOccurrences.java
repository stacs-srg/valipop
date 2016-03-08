package model.implementation.occurrencesInformation;

import model.enums.EventType;
import model.enums.Gender;
import model.interfaces.dataStores.PopulationInformationCollection;
import model.interfaces.dataStores.informationAccess.QuantifiedEventAccess;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;
import model.interfaces.dataStores.informationPassing.tableTypes.TwoWayTable;

/**
 * The QuantifiedEventOccurrences holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class QuantifiedEventOccurrences implements PopulationInformationCollection, QuantifiedEventAccess {


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
    public TwoWayTable<Double> getMarriageRates(int year) {
        return null;
    }

    @Override
    public OneWayTable<Double> getBirthRates(int year) {
        return null;
    }

    @Override
    public TwoWayTable<Double> getBirthRatesByOrder(int year) {
        return null;
    }

    @Override
    public OneWayTable<Integer> getSurvivorTable(int startYear, int timePeriod, EventType event) {
        return null;
    }
}
