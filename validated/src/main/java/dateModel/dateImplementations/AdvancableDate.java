package dateModel.dateImplementations;

import dateModel.Date;
import dateModel.exceptions.UnsupportedDateConversion;
import dateModel.timeSteps.CompoundTimeUnit;
import dateModel.timeSteps.TimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface AdvancableDate extends Date {

    AdvancableDate advanceTime(int numberOf, TimeUnit unit);

    AdvancableDate advanceTime(CompoundTimeUnit timeStep);

    MonthDate getMonthDate();


}
