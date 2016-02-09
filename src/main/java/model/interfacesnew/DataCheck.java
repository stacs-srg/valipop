package model.interfacesnew;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataCheck {

    CheckResult[] getChecks();

    CheckResult[] getMissingChecks();

    CheckResult[] getWarningChecks();

    CheckResult[] getFailedChecks();

    boolean passed();

    boolean warnings();

    boolean missing();

    boolean failures();

}
