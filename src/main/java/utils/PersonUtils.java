package utils;

import model.IPartnership;
import model.IPerson;
import model.NoChildrenOfDesiredOrder;
import model.NotDeadException;
import utils.time.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonUtils {

    boolean noRecentChildren(DateClock currentDate, CompoundTimeUnit timePeriod);

    void recordPartnership(IPartnership partnership);

    void recordDeath(Date date);

    void causeDeathInTimePeriod(Date date, CompoundTimeUnit timePeriod);

    int ageAtDeath() throws NotDeadException;

    boolean aliveOnDate(Date date);

    int ageAtFirstChild() throws NoChildrenOfDesiredOrder;

    IPerson getLastChild();

}
