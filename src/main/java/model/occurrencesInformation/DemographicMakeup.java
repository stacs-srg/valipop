package model.occurrencesInformation;

import model.interfaces.dataStores.PopulationInformationCollection;
import model.interfaces.dataStores.informationFlow.query.Query;
import model.interfaces.dataStores.informationFlow.result.QueryResult;

/**
 * The data store is a base of data that describes basic characteristics of the summative population as a whole by year,
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
 * The Demographic Data Store should be set up so that it defines a time period to which it pertains.
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
 * Given the many years a data store will likely hold data for it is likely that not all the data will be available
 * over the whole time period, therefore an imputation approach is provided to assign values to empty fields.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DemographicMakeup implements PopulationInformationCollection {


    @Override
    public int getEarliestDay() {
        return 0;
    }

    @Override
    public int getLatestDay() {
        return 0;
    }

    @Override
    public QueryResult getInfo(Query query) {
        return null;
    }
}
