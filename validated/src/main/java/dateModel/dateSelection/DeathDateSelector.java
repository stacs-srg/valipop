package dateModel.dateSelection;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;
import simulationEntities.person.IPerson;

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

    @Override
    public ExactDate selectDateLPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date latestPossibleDate) {

        // if specified latestPossibleDate is in consideredTimePeriod
        if (DateUtils.dateBefore(latestPossibleDate, currentDate.advanceTime(consideredTimePeriod))) {
            // then select date between currentDate and latestPossible date
            int days = DateUtils.differenceInDays(currentDate, latestPossibleDate);
            int chosenDay = random.nextInt(Math.abs(days));
            return DateUtils.calculateExactDate(currentDate, chosenDay);
        } else {
            // else all days in consideredTimePeriod are an option
            return selectDateUnrestricted(currentDate, consideredTimePeriod);
        }

    }

    @Override
    public ExactDate selectDateEPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date earliestPossibleDate) {

        // if specified earliestPossibleDate is in consideredTimePeriod
        if(DateUtils.dateBefore(currentDate, earliestPossibleDate)) {
            // The select date between earliestPossibleDate and currentDate + consideredTimePeriod
            int days = DateUtils.differenceInDays(earliestPossibleDate, currentDate.advanceTime(consideredTimePeriod));
            int chosenDay = random.nextInt(Math.abs(days));
            return DateUtils.calculateExactDate(currentDate, chosenDay);
        } else {
            // else all days in consideredTimePeriod are an option
            return selectDateUnrestricted(currentDate, consideredTimePeriod);
        }

    }

    @Override
    public ExactDate selectDate(IPerson p, Config config, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod) {

        IPerson child = p.getLastChild();

        if(child != null) {

            Date birthDateOfLastChild = child.getBirthDate().getExactDate();

            if (Character.toLowerCase(p.getSex()) == 'm') {
                Date ePD = DateUtils.calculateExactDate(birthDateOfLastChild, (-1) * config.getMaxGestationPeriod());
                return selectDateEPD(currentDate, consideredTimePeriod, ePD);
            } else {
                return selectDateEPD(currentDate, consideredTimePeriod, birthDateOfLastChild);
            }

        } else {
            return selectDateUnrestricted(currentDate, consideredTimePeriod);
        }

    }

    private ExactDate selectDateUnrestricted(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod) {
        int days = DateUtils.getDaysInTimePeriod(currentDate, consideredTimePeriod);
        int chosenDay = random.nextInt(Math.abs(days));
        return DateUtils.calculateExactDate(currentDate, chosenDay);
    }


}
