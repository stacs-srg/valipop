package dateModel.dateSelection;

import config.Config;
import dateModel.Date;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;
import simulationEntities.person.IPerson;
import simulationEntities.person.Person;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DateSelector {

    ExactDate selectDate(Date possibleDate, CompoundTimeUnit consideredTimePeriod);

    ExactDate selectDate(Date possibleDate, CompoundTimeUnit consideredTimePeriod, int imposedLimit);

    ExactDate selectDateLPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date latestPossibleDate);

    ExactDate selectDateEPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date earliestPossibleDate);

    ExactDate selectDate(IPerson p, Config config, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod);

}
