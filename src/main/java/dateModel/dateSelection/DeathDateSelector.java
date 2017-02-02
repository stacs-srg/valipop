package dateModel.dateSelection;

import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathDateSelector implements DateSelector {

    private Random random = new Random();

    @Override
    public ExactDate selectDate(Date latestPossibleDate, CompoundTimeUnit consideredTimePeriod) {

        int possibleDays = DateUtils.getDaysInTimePeriod(latestPossibleDate, consideredTimePeriod.negative());

        int chosenDay = (-1) * random.nextInt(Math.abs(possibleDays));

        return DateUtils.calculateExactDate(latestPossibleDate, chosenDay);

    }

    @Override
    public ExactDate selectDate(Date latestPossibleDate, CompoundTimeUnit consideredTimePeriod, int imposedLimit) {

        if(imposedLimit == 0) {
            return latestPossibleDate.getExactDate();
        }

        int chosenDay = (-1) * random.nextInt(Math.abs(imposedLimit));

        return DateUtils.calculateExactDate(latestPossibleDate, chosenDay);

    }


}
