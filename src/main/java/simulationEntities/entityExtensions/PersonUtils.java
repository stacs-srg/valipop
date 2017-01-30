package simulationEntities.entityExtensions;

import events.EventType;
import events.birth.NoChildrenOfDesiredOrder;
import events.death.NotDeadException;
import simulationEntities.IPartnership;
import simulationEntities.IPerson;
import dateModel.Date;
import dateModel.dateImplementations.DateClock;
import dateModel.timeSteps.CompoundTimeUnit;
import simulationEntities.population.dataStructure.PeopleCollection;

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

    void keepFather(PeopleCollection population);

    void setParentsPartnership(IPartnership newParents);

    int numberOfChildrenFatheredChildren();

    IPartnership isInstigatorOfSeparationOfMothersPreviousPartnership();

    boolean isWidow(Date onDate);

    IPerson getPartner(Date onDate);

}
