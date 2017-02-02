package dateModel.dateSelection;

import dateModel.Date;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DateSelector {

    ExactDate selectDate(Date possibleDate, CompoundTimeUnit consideredTimePeriod);

    ExactDate selectDate(Date possibleDate, CompoundTimeUnit consideredTimePeriod, int imposedLimit);

}
