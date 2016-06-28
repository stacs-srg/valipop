package model.dateSelection;

import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.DateUtils;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthDateSelector implements DateSelector {


    @Override
    public Date selectDate(Date earliestPossibleDate, CompoundTimeUnit consideredTimePeriod) {

        // TODO add in handling of date instants
        // get number of days in period of consideration
        int daysInTimePeriod = DateUtils.getDaysInTimePeriod(earliestPossibleDate, consideredTimePeriod);

        // choose a day - at random for now
        // TODO keep writing!!

        // turn chosen day number into a valid date


        // return chosen valid date

        return null;
    }
}
