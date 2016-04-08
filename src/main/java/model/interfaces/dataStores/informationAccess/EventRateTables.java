package model.interfaces.dataStores.informationAccess;

import model.enums.Gender;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;
import model.interfaces.dataStores.informationPassing.tableTypes.TwoWayTable;

/**
 * The EventRateTables interface provides methods that pertain to the events modelled within the population
 * simulation. The fact they are 'quantified' means that they are expressed as rates or a standardised format where
 * otherwise indicated.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface EventRateTables {

    /**
     * Gets death rates for people at each age for the current given year of the specified gender. The death rates are
     * expressed as a proportion.
     *
     *               | ASDR
     *          -------------
     *            0  | 0.203
     *  Current   1  | 0.102
     *    age     2  | 0.001
     *           ... |  ...
     *
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
     *                        Age of Male
     *
     *                 |   16  |   17  |   18  |  ...
     *             ------------------------------------
     *              16 | 0.003 | 0.002 | 0.200 |  ...
     *   Age of     17 | 0.102 | 0.012 | 0.103 |  ...
     *   female     18 | 0.109 | 0.131 | 0.171 |  ...
     *              .. |  ...  |  ...  |  ...  |
     *
     *
     * @param year the year
     * @return the marriage rates
     */
    TwoWayTable<Double> getPartneringRates(int year);

    /**
     * Gets birth rates for births in the given year defined by the age of the mother.
     *
     *               | ASBR
     *          -------------
     *            16 | 0.203
     *   Female   17 | 0.102
     *    age     18 | 0.001
     *            .. |  ...
     *
     * @param year the year
     * @return the birth rates
     */
    OneWayTable<Double> getBirthRates(int year);

    /**
     * Gets birth rates by order for births in the given year defined by the age and number of previous children born to
     * the mother.
     *
     *                        Birth Order
     *
     *                 |   0   |   1   |   2   |  ...
     *             ------------------------------------
     *              16 | 0.003 | 0.002 | 0.000 |  ...
     *   Age of     17 | 0.052 | 0.012 | 0.003 |  ...
     *   female     18 | 0.109 | 0.041 | 0.021 |  ...
     *              .. |  ...  |  ...  |  ...  |
     *
     * @param year the year
     * @return the birth rates by order
     */
    TwoWayTable<Double> getOrderedBirthRates(int year);

    /**
     * Gets multiple births in a maternity rate for women giving birth in a given year by age of the mother.
     *
     *       Number of live children resulting from maternity
     *
     *                 |   1   |   2   |   3   |  ...
     *             ------------------------------------
     *              16 | 0.003 | 0.002 | 0.000 |  ...
     *   Age of     17 | 0.052 | 0.012 | 0.003 |  ...
     *   female     18 | 0.109 | 0.041 | 0.021 |  ...
     *              .. |  ...  |  ...  |  ...  |
     *
     * @param year the year
     * @return the birth rates by order
     */
    TwoWayTable<Double> getMultipleBirthRates(int year);

    /**
     * Gets the rate of separation after having a given number of children as a couple. The rate is considered in respect
     * to the whole female population.
     *
     *               | Rate
     *          -------------
     *            1  | 0.03
     *  Current   2  | 0.02
     *    age     3  | 0.01
     *           ... |  ...
     *
     *
     * @param year   the year
     * @return the death rates
     */
    OneWayTable<Double> getSeparationByChildCountRates(int year);

}
