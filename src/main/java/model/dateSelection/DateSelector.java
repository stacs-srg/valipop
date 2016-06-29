package model.dateSelection;

import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.DateInstant;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DateSelector {

    DateInstant selectDate(Date earliestPossibleDate, CompoundTimeUnit consideredTimePeriod);

}
