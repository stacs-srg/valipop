package utils;

import datastructure.summativeStatistics.generated.EventType;
import model.simulationEntities.IPartnership;
import model.simulationEntities.IPerson;
import model.exceptions.NoChildrenOfDesiredOrder;
import model.exceptions.NotDeadException;
import utils.time.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonUtils {

    boolean noRecentChildren(DateClock currentDate, CompoundTimeUnit timePeriod);

    void recordPartnership(IPartnership partnership);

    void recordDeath(Date date);

    void causeEventInTimePeriod(EventType event, Date date, CompoundTimeUnit timePeriod);

    int ageAtDeath() throws NotDeadException;

    boolean aliveOnDate(Date date);

    int ageAtFirstChild() throws NoChildrenOfDesiredOrder;

    IPerson getLastChild();

    int numberOfChildren();

    void keepFather();

    void setParentsPartnership(IPartnership newParents);

    int numberOfChildrenFatheredChildren();

    IPartnership isInstigatorOfSeparationOfMothersPreviousPartnership();

    boolean isWidow(Date onDate);

    IPerson getPartner(Date onDate);

}
