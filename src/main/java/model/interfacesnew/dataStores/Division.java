package model.interfacesnew.dataStores;

/**
 * A Division represents an interval with a minimum value (inclusive, i.e. age) and a maximum value (exclusive, i.e.
 * age) with a number assigned to this interval to indicate the number of individuals in this interval who appear in the
 * population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Division {

    /**
     * Gets the minimum value/age (inclusive) represented by this division.
     *
     * @return the minimum value/age in days
     */
    int getMinValue();

    /**
     * Gets the maximum value/age (exclusive) represented in this division.
     *
     * @return the maximum value/age in days
     */
    int getMaxValue();


    /**
     * Gets the number of individuals in the divison.
     *
     * @return the number of individuals in the division
     */
    int getNumber();

}
