package model.interfacesnew.dataStores;

/**
 * A Division represents an interval with a given identifier (this could be set to be a numerical interval e.g. a minimum
 * value (inclusive, i.e. age) and a maximum value (exclusive, i.e. age) with a number assigned to this interval to
 * indicate the number of individuals in this interval who appear in the population. There is also a secondary Division
 * available within a Division that can allow for more nuanced data (e.g. the total number may represent the number of
 * women in the wider interval bearing children but the order of birth (i.e how many children a woman has had previous
 * to this child)) can be modelled using the secondary Division functionality.
 *
 * @param <P> the type of the primary Division identifier
 * @param <S> the type of the secondary Division identifier
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Division<P, S> {

    /**
     * Gets division identifier.
     *
     * @return the division identifier
     */
    P getDivisionIdentifier();

    /**
     * Gets the number of individuals in the division.
     *
     * @return the number of individuals in the division
     */
    int getTotalNumber();


    /**
     * Get the set of Divisions which gives a second layer of division of the data space.
     *
     * @return the set of dividers
     */
    Division<S, ?>[] getSecondaryDivisions();



}
