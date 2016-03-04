package model.interfaces.dataStores.informationAccess;

import model.enums.EventType;
import model.enums.Gender;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;
import model.interfaces.dataStores.informationPassing.tableTypes.TwoWayTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface QuantifiedEventAccess {

    OneWayTable<Double> getDeathRates(int year, Gender gender);

    TwoWayTable<Double> getMarriageRates(int year);

    OneWayTable<Double> getBirthRates(int year);

    TwoWayTable<Double> getBirthRatesByOrder(int year);

    OneWayTable<Integer> getSurvivorTable(int startYear, int timePeriod, EventType event);

}
