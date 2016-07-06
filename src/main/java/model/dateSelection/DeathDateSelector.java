package model.dateSelection;

import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.DateInstant;
import utils.time.DateUtils;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathDateSelector implements DateSelector {

    private Random random = new Random();

    @Override
    public DateInstant selectDate(Date latestPossibleDate, CompoundTimeUnit consideredTimePeriod) {

        int possibleDays = DateUtils.getDaysInTimePeriod(latestPossibleDate, consideredTimePeriod.negative());

        int chosenDay = (-1) * random.nextInt(Math.abs(possibleDays));

        return DateUtils.calculateDateInstant(latestPossibleDate, chosenDay);

    }

    @Override
    public DateInstant selectDate(Date latestPossibleDate, CompoundTimeUnit consideredTimePeriod, int imposedLimit) {

        if(imposedLimit == 0) {
            return latestPossibleDate.getDateInstant();
        }

        int chosenDay = (-1) * random.nextInt(Math.abs(imposedLimit));

        return DateUtils.calculateDateInstant(latestPossibleDate, chosenDay);

    }


}
