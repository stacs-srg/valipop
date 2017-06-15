package uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DateSelector {

    ExactDate selectDate(Date possibleDate, CompoundTimeUnit consideredTimePeriod);

    ExactDate selectDate(Date possibleDate, CompoundTimeUnit consideredTimePeriod, int imposedLimit);

    ExactDate selectDateLPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date latestPossibleDate);

    ExactDate selectDateEPD(AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod, Date earliestPossibleDate);

    ExactDate selectDate(IPerson p, PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod);

}
