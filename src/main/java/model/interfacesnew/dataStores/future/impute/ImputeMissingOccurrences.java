package model.interfacesnew.dataStores.future.impute;

import model.interfacesnew.dataStores.PopulationInformationCollection;

/**
 * The interface Imputable data store.
 *
 * ----- Calculation and imputation methods -----
 *
 *  NOTE: There is a difference between calculation and imputation.
 *  Calculating data means we are filling in fields based on information we hold in other fields but the same year
 *  e.g. calculating the male to female ratio for 1992 using the data we hold about the size of the male and
 *  female populations in 1992
 *
 *  Imputing data means we are filling in fields based on information we hold in the same field but for different
 *  years
 *      e.g. imputing that the size of the male population is 1000 in 1992 based on the information that the male
 *      population in 1991 and 1993 was 950 and 1050 respectively.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ImputeMissingOccurrences {

    // TODO needs to take a Factory as a parameter - need a better understanding of the unifying features of an InformationFactory before we can do this though.
    PopulationInformationCollection imputeInformation();

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
