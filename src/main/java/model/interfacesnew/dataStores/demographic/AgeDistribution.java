package model.interfacesnew.dataStores.demographic;

import model.interfacesnew.dataStores.general.Division;

/**
 * The age distribution object provides a mechanism to handle the number of people who fall into a given age bracket in
 * a population [subset] for a time period in the data store.
 *
 * An example of its use may be to calculate the number of females aged between 20-25 were alive in 1980 in the
 * summative (i.e. the data we use as the basis for our model) population that could then be used with information about
 * the number of babies born to women of this age in the given year so as to calculate a rate.
 *
 * NOTE: This is not a distribution in the style of the earlier population models where distributions were used to
 * enforce statistical distributions - rather these distributions are better thought of as a way to represent data found
 * in population pyramids in an OO way.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface AgeDistribution {

    /**
     * Gives a set of divisions each representing a tier of the population pyramid.
     *
     * @return a set of divisions
     */
    Division[] getAgeDivisions();

    /**
     * Allows foe the divisions to be set which represent each tier of the population pyramid.
     *
     * @param divisions a set of divisions
     */
    void setAgeDivisions(Division[] divisions);

}
