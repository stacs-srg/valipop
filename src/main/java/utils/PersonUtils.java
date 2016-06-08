package utils;

import model.IPartnership;
import model.IPerson;
import model.NotDeadException;
import utils.time.Date;
import utils.time.DateClock;
import utils.time.DateUtils;
import utils.time.TimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonUtils {

    boolean noRecentChildren(DateClock currentDate);

    void recordPartnership(IPartnership partnership);

    void recordDeath(Date date);

    int ageAtDeath() throws NotDeadException;


}
