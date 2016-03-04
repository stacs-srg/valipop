package model.interfaces.dataStores.informationFactories;

import model.occurrencesInformation.DemographicMakeup;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DemographicMakeupFactory {

    DemographicMakeup makeDemographicMakeup();

    /*
    ----- Insertion methods -----
     */

    /**
     * Add data in the form year, population size, male to female ratio.
     *
     * @param year           the year
     * @param populationSize the population size
     * @param ratio          the ratio
     */
    void addDataYPR(int year, int populationSize, double ratio);

    /**
     * Add data in the form year, male population size, female population size.
     *
     * @param year                 the year
     * @param malePopulationSize   the male population size
     * @param femalePopulationSize the female population size
     */
    void addDataYMF(int year, int malePopulationSize, int femalePopulationSize);

    /**
     * Add data in the form start year, end year, array of population sizes with a value for each stated year inclusive.
     *
     * @param startYear       the start year
     * @param endYear         the end year
     * @param populationSizes the population sizes
     */
    void addDataSEP(int startYear, int endYear, int[] populationSizes);

    /**
     * Add data in the form start year, end year, array of male to female ratios with a value for each stated year
     * inclusive.
     *
     * @param startYear the start year
     * @param endYear   the end year
     * @param ratios    the ratios
     */
    void addDataSER(int startYear, int endYear, int[] ratios);

    /**
     * Add data in the form start year, end year, array of population sizes and an array of male to female ratios, with
     * a value for each stated year inclusive.
     *
     * @param startYear       the start year
     * @param endYear         the end year
     * @param populationSizes the population sizes
     * @param ratios          the male to female ratios
     */
    void addDataSEPR(int startYear, int endYear, int[] populationSizes, int[] ratios);

}
