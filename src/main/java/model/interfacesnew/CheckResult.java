package model.interfacesnew;

import model.enums.DataField;
import model.enums.Status;

/**
 * A Check Results holds information about a test on a single field for the specified year.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface CheckResult {

    /**
     * The tests result Status.
     *
     * @return the test's status
     */
    Status status();

    /**
     * The year to which the test pertains.
     *
     * @return the year to which the test pertains
     */
    int year();

    /**
     * The data field to which the test pertains.
     *
     * @return the data field to which the test pertains
     */
    DataField field();

    /**
     * Further detail regarding the reason for the test's result.
     *
     * @return futher detail for the test's result
     */
    String detail();

}
