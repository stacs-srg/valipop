package model.interfacesnew;

import model.enums.Status;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface CheckResult {

    Status status();

    int year();

    String detail();


}
