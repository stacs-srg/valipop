package simulationEntities.entityExtensions;

import dateModel.dateImplementations.AdvancableDate;
import events.EventType;
import events.birth.NoChildrenOfDesiredOrder;
import events.death.NotDeadException;
import simulationEntities.partnership.IPartnership;
import simulationEntities.person.IPerson;
import dateModel.Date;
import dateModel.dateImplementations.MonthDate;
import dateModel.timeSteps.CompoundTimeUnit;
import simulationEntities.population.dataStructure.PeopleCollection;
import simulationEntities.population.dataStructure.Population;
import utils.specialTypes.integerRange.IntegerRange;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonUtils {

    boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod);

    void recordPartnership(IPartnership partnership);

    boolean recordDeath(Date date, Population population);

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

    void giveChildren(int numberOfChildren, AdvancableDate onDate, CompoundTimeUnit birthTimeStep, Population population);

    boolean toSeparate();

    void willSeparate(boolean b);

    int ageOnDate(Date date);

    boolean needsPartner(AdvancableDate currentDate);

    int numberOfChildrenInLatestPartnership();

    Collection<IPerson> getAllChildren();

    Collection<IPerson> getAllGrandChildren();

    Collection<IPerson> getAllGreatGrandChildren();
}
