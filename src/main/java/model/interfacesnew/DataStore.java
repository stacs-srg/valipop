package model.interfacesnew;

/**
 * The data store is a base of data that describes primary characteristics of the population as a whole by year,
 * currently these include:
 * <ul>
 * <li>Total population size (?) at mid year</li>
 * <li>Male population size</li>
 * <li>Female population size</li>
 * <li>Male to female ratio</li>
 * <li>Male ages distribution</li>
 * <li>Female ages distribution</li>
 * </ul>
 * <p>
 * The DataStore should be set up so that it defines a time period to which it pertains.
 * <p>
 * The insertion of data into the store is handled by a set of methods that allow data to be added in different
 * combinations, time periods and formats.
 * <p>
 * Based on the variety of ways and the sporadicity with which this data is published the store is set up to allow for
 * the data to be given in a differing formats, even within a single store, and then for the remaining fields to be
 * calculated within each year. Given the fact data can be given in differing formats, in the case where both formats
 * are given but they differ then the data store is set by default to enforce values over ratios, however, this can be
 * modified.
 * <p>
 * Given the many years a data store will likely hold data for it is likely that not all the data will be availiable
 * over the whole time period, therefore an imputation approach is provided to assign values to empty fields.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataStore {

    /*
    ----- Utility parameters -----
     */

    /**
     * Gets earliest day that this Data Store is required to provide information regarding.
     *
     * @return the earliest day
     */
    int getEarliestDay();

    /**
     * Gets latest day that this Data Store is required to provide information regarding.
     *
     * @return the latest day
     */
    int getLatestDay();

    /**
     * Enforces the use of ratios to calculate male/female populations over the use of given figures, even in the
     * presence of value data that has been inserted into the data store. The default behaviour is that value based data
     * should be used.
     *
     * @param b bollean indicating the enforcement of the use of ratios
     */
    void enforceRatioOverValues(boolean b);

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

    /*
    ----- Data checking methods -----
     */

    /**
     * Checks that data is present (given, calculated or imputed) for each year from the start date to the end date for
     * every field.
     *
     * @return indicates whether all data is present
     */
    boolean allDataPresent();

    /**
     * Checks that data is present (given, calculated or imputed) for the specified year for every field.
     *
     * @param year a year in the data store
     * @return indicates whether all data is present
     */
    boolean dataPresent(int year);

    /**
     * Checks that data is present (given, calculated or imputed) for each year from the start date to the end date for
     * every field and returns details of every check made and its results for each field in each year.
     *
     * @return a DataCheck object containing all checks made and there results including the way in which the data was
     * specified.
     */
    DataCheck checkData();

    /**
     * Checks that data is present (given, calculated or imputed) for the given year for every field and returns details
     * of every check made and its results for each field in each year.
     *
     * @param year the year
     * @return a DataCheck object containing all checks made and there results including the way in which the data was
     * specified.
     */
    DataCheck checkData(int year);

    /*
    ----- Calculation and imputation methods -----

        NOTE: There is a difference between calculation and imputation.
        Calculating data means we are filling in fields based on information we hold in other fields but the same year
            e.g. calculating the male to female ratio for 1992 using the data we hold about the size of the male and
            female populations in 1992

        Imputing data means we are filling in fields based on information we hold in the same field but for different
        years
            e.g. imputing that the size of the male population is 1000 in 1992 based on the information that the male
            population in 1991 and 1993 was 950 and 1050 respectively.
     */

    /**
     * Calculates any missing fields in the data store which it is possible to calculate from the data already residing
     * in the data store.
     */
    void calculateData();

    /**
     * Calculates any missing fields, for a given year, in the data store which it is possible to calculate from the
     * data already residing in the data store for that year.
     *
     * @param year the year
     */
    void calculateData(int year);

    /**
     * Imputes data for any missing fields in the data store which is possible to make an imputation for using data
     * already residing in the data store and based on (?) a given set of parameters.
     */
    void imputeData();

    /**
     * Imputes data for any missing fields, for a given year, in the data store which is possible to make an imputation
     * for using data already residing in the data store for any year and based on (?) a given set of parameters.
     *
     * @param year the year
     */
    void imputeData(int year);

}
