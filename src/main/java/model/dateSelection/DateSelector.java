package model.dateSelection;

import utils.time.CompoundTimeUnit;
import utils.time.Date;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DateSelector {

    Date selectDate(Date earliestPossibleDate, CompoundTimeUnit consideredTimePeriod);

}
