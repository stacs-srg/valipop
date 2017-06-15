package uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;


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

    @Override
    public ExactDate selectDateLPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date latestPossibleDate) {
        return null;
    }

    @Override
    public ExactDate selectDateEPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date earliestPossibleDate) {
        return null;
    }

    @Override
    public ExactDate selectDate(IPerson p, PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod) {
        return null;
    }
}
