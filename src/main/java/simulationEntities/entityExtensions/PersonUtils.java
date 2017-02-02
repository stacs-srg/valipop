package simulationEntities.entityExtensions;

import events.EventType;
import events.birth.NoChildrenOfDesiredOrder;
import events.death.NotDeadException;
import simulationEntities.partnership.IPartnership;
import simulationEntities.person.IPerson;
import dateModel.Date;
import dateModel.dateImplementations.MonthDate;
import dateModel.timeSteps.CompoundTimeUnit;
import simulationEntities.population.PopulationCounts;
import simulationEntities.population.dataStructure.PeopleCollection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonUtils {

    boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod);

    void recordPartnership(IPartnership partnership);

    void recordDeath(Date date, PopulationCounts pc);

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
