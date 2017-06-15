package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.entityExtensions;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.birth.NoChildrenOfDesiredOrder;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.death.NotDeadException;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;

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

    void giveChildrenWithinLastPartnership(int numberOfChildren, AdvancableDate onDate, CompoundTimeUnit birthTimeStep, Population population);

    boolean toSeparate();

    void willSeparate(boolean b);

    int ageOnDate(Date date);

    boolean needsNewPartner(AdvancableDate currentDate);

    int numberOfChildrenInLatestPartnership();

    Collection<IPerson> getAllChildren();

    Collection<IPerson> getAllGrandChildren();

    Collection<IPerson> getAllGreatGrandChildren();
}
