package model.interfaces.dataStores.informationAccess;

import model.enums.EventType;
import model.enums.Gender;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;
import model.interfaces.dataStores.informationPassing.tableTypes.TwoWayTable;

/**
 * The QuantifiedEventAccess interface provides methods that pertain to the events modelled within the population
 * simulation. The fact they are 'quantified' means that they are expressed as rates or a standardised format where
 * otherwise indicated.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface QuantifiedEventAccess {

    /**
     * Gets death rates for those born in a given year over the following 100 years of the specified gender. The death
     * rates are expresed as a proportation of a 1000.
     *
     * @param year   the year
     * @param gender the gender
     * @return the death rates
     */
    OneWayTable<Double> getDeathRates(int year, Gender gender);

    /**
     * Gets marriage rates for those married in the given year. The return table is two dimensional as it shows the rate
     * at which a male of age X marries a female of age Y in the given year.
     *
     * @param year the year
     * @return the marriage rates
     */
    TwoWayTable<Double> getMarriageRates(int year);

    /**
     * Gets birth rates for births in the given year defined by the age of the mother.
     *
     * @param year the year
     * @return the birth rates
     */
    OneWayTable<Double> getBirthRates(int year);

    /**
     * Gets birth rates by order for births in the given year defined by the age and number of previous children born to
     * the mother.
     *
     * @param year the year
     * @return the birth rates by order
     */
    TwoWayTable<Double> getBirthRatesByOrder(int year);

    /**
     * Gets the survivor table for the given event across the specified time period. A survivor table gives the number
     * in the risk group in the startYear and then for each year shows how many people remain in the risk group but to
     * whom the event has not happened since the startYear.
     *
     * @param startYear  the start year
     * @param timePeriod the time period
     * @param event      the event
     * @return the survivor table
     */
    OneWayTable<Integer> getSurvivorTable(int startYear, int timePeriod, EventType event);

}
