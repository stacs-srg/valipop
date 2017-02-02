package dateModel.dateSelection;

import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;


import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthDateSelector implements DateSelector {

    private Random random = new Random();

    @Override
    public ExactDate selectDate(Date startingDate, CompoundTimeUnit consideredTimePeriod) {

        // get number of days in period of consideration
        int daysInTimePeriod = DateUtils.getDaysInTimePeriod(startingDate, consideredTimePeriod);

        // choose a day - at random for now
        int chosenDay = random.nextInt(daysInTimePeriod);

        // turn chosen day number into a valid date
        ExactDate chosenDate = DateUtils.calculateExactDate(startingDate, chosenDay);

        // return chosen valid date

        return chosenDate;
    }

    @Override
    public ExactDate selectDate(Date possibleDate, CompoundTimeUnit consideredTimePeriod, int imposedLimit) {
        return selectDate(possibleDate, consideredTimePeriod);
    }
}
