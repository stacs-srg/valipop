package model.interfacesnew.dataStores.demographic;

/**
 * The interface Demographic variables.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DemographicVariables {

    /*
    ----- Retrieval methods -----
     */

    /**
     * Gets population size.
     *
     * @param year the year
     * @return the population size
     */
    int getPopulationSize(int year);

    /**
     * Gets male population size.
     *
     * @param year the year
     * @return the male population size
     */
    int getMalePopulationSize(int year);

    /**
     * Gets female population size.
     *
     * @param year the year
     * @return the female population size
     */
    int getFemalePopulationSize(int year);

    /**
     * Gets male to female ratio.
     *
     * @param year the year
     * @return the male female ratio
     */
    double getMaleFemaleRatio(int year);

    /**
     * Gets male age distribution. This is synonymous to the information seen represented in a population pyramid.
     *
     * @param year the year
     * @return the male age distribution
     */
    AgeDistribution getMaleAgeDistribution(int year);

    /**
     * Gets female age distribution. This is synonymous to the information seen represented in a population pyramid.
     *
     * @param year the year
     * @return the female age distribution
     */
    AgeDistribution getFemaleAgeDistribution(int year);

}
