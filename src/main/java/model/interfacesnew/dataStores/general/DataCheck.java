package model.interfacesnew.dataStores.general;

/**
 * The Data Check holds all the information about a set of Check Results of checks ran on the Data Store. These checks
 * cover:
 * <ul>
 *     <li>Passed - data present and reasonable</li>
 *     <li>Missing - empty fields in the data store</li>
 *     <li>Warning - where data appears to be unlikely across years, these though could be mitigated by extreme events
 *     such as war, famine or mass migration</li>
 *     <li>Failed - where data within years that is directly calculable contradicts each another.</li>
 * </ul>
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataCheck {

    /**
     * Gets the set of all Check Results.
     *
     * @return all Check Results in this Data Check
     */
    CheckResult[] getChecks();

    /**
     * Gets the passed Check Results.
     *
     * @return the passed Check Results in this Data Check
     */
    CheckResult[] getPassedChecks();

    /**
     * Gets the missing Check Results.
     *
     * @return the missing Check Results in this Data Check
     */
    CheckResult[] getMissingChecks();

    /**
     * Gets the warning Check Results.
     *
     * @return the warning Check Results in this Data Check
     */
    CheckResult[] getWarningChecks();

    /**
     * Gets the failed Check Results.
     *
     * @return the failed Check Results in this Data Check
     */
    CheckResult[] getFailedChecks();

    /**
     * Returns true if all tests have passed.
     *
     * @return true if all test have passed
     */
    boolean passed();

    /**
     * Returns true is warnings are present.
     *
     * @return true if warnings are present
     */
    boolean warnings();

    /**
     * Returns true if missing check result present.
     *
     * @return true if missing check result present
     */
    boolean missing();

    /**
     * Returns true if failures are present.
     *
     * @return true if failures are present.
     */
    boolean failures();

}
