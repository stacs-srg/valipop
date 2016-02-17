package model.interfacesnew.dataStores.values;

import model.interfacesnew.dataStores.general.Division;

/**
 * A NumberTable holds a set of divisions which allow for the storage of values associated with a set of primary
 * dividers.
 * <p>
 * The year to which the NumberTable refers is handled in a higher level data store.
 * <p>
 * For example a NumberTable concerned with the number of women who bear children at a given age would make
 * use of the primary type (P) to set the ranges of each age division and then a total type (T) of int, so as to be able
 * to express the number of women in a given age range who gave birth. The secondary type (S) can be used to subdivide
 * the women in each of the age ranges (in this case), for example, by birth order.
 * <p>
 * The NumberTable can also be used with different T types, for example a double could be used to represent a rate, e.g.
 * a proportion out of a thousand.
 *
 * @param <P> the primary type parameter
 * @param <S> the secondary type parameter
 * @param <T> the total type parameter
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface NumberTable<P, S, T> {

    /**
     * Returns the set of divisions that make up this NumberTable.
     *
     * @return the set of divisions that make up this NumberTable
     */
    Division<P, S, T>[] getDivisions();

    /**
     * Sets the Divisions for this NumberTable.
     *
     * @param divisions the Divisions for this NumberTable.
     */
    void setDivisions(Division<P, S, T>[] divisions);

    /**
     * Returns the total value for the given primary parameter.
     *
     * @param primary the primary parameter of interest
     * @return the total value
     */
    T getValue(P primary);

    /**
     * Returns the total value for the given secondary parameter of the spicified primary parameter.
     *
     * @param primary   the primary parameter of interest
     * @param secondary the secondary parameter of interest
     * @return the total value
     */
    T getValue(P primary, S secondary);

}
