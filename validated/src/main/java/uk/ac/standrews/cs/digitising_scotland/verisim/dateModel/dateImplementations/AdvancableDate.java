package uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface AdvancableDate extends Date {

    AdvancableDate advanceTime(int numberOf, TimeUnit unit);

    AdvancableDate advanceTime(CompoundTimeUnit timeStep);

    MonthDate getMonthDate();


}
