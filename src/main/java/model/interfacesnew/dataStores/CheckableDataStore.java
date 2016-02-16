package model.interfacesnew.dataStores;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface CheckableDataStore {

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

}
