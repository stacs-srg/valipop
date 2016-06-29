package model.dateSelection;

import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.DateInstant;
import utils.time.DateUtils;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthDateSelector implements DateSelector {

    private Random random = new Random();

    @Override
    public DateInstant selectDate(Date startingDate, CompoundTimeUnit consideredTimePeriod) {

        // get number of days in period of consideration
        int daysInTimePeriod = DateUtils.getDaysInTimePeriod(startingDate, consideredTimePeriod);

        // choose a day - at random for now
        int chosenDay = random.nextInt(daysInTimePeriod);

        // turn chosen day number into a valid date
        DateInstant chosenDate = DateUtils.calculateDateInstant(startingDate, chosenDay);

        // return chosen valid date

        return null;
    }
}
