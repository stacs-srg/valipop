package model.simulationEntities;

import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.generated.EventType;
import model.dateSelection.BirthDateSelector;
import model.dateSelection.DateSelector;
import model.dateSelection.DeathDateSelector;
import model.exceptions.NoChildrenOfDesiredOrder;
import model.exceptions.NotDeadException;
import model.simulationLogic.Simulation;
import model.simulationLogic.stochastic.PopulationCounts;
import utils.time.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import verify.Verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Person implements IPerson {

    private static Logger log = LogManager.getLogger(Person.class);
    private static int nextId = 0;
    private int id;
    private char sex;
    private DateInstant birthDate;
    private DateInstant deathDate;
    private List<IPartnership> partnerships = new ArrayList<>();
    private IPartnership parentsPartnership;
    private String firstName;
    private String surname;

    private DateSelector deathDateSelector = new DeathDateSelector();
    private DateSelector birthDateSelector = new BirthDateSelector();


    public Person(char sex, Date birthDate) {
        id = getNewId();
        this.sex = Character.toLowerCase(sex);
        this.birthDate = birthDate.getDateInstant();
    }

    public Person(char sex, Date birthDate, IPartnership parentsPartnership) {
        id = getNewId();
        this.sex = Character.toLowerCase(sex);
        this.birthDate = birthDate.getDateInstant();
        this.parentsPartnership = parentsPartnership;
    }

    private static int getNewId() {
        return nextId++;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public char getSex() {
        return sex;
    }

    @Override
    public DateInstant getBirthDate() {
        return birthDate;
    }

    @Override
    public DateInstant getDeathDate() {
        return deathDate;
    }

    @Override
    public List<IPartnership> getPartnerships() {
        return partnerships;
    }

    @Override
    public IPartnership getParentsPartnership() {
        return parentsPartnership;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    // TODO Implement geography model
    @Override
    public String getBirthPlace() {
        return null;
    }

    @Override
    public String getDeathPlace() {
        return null;
    }

    // TODO Implement occupation assignment
    @Override
    public String getOccupation() {
        return null;
    }

    // TODO Implement death causes - does occupation, date, gender, location, etc. influence this?
    @Override
    public String getDeathCause() {
        return null;
    }

    @Override
    public int compareTo(IPerson o) {
        return this.id == o.getId() ? 0 : -1;
    }

    @Override
    public boolean noRecentChildren(DateClock currentDate, CompoundTimeUnit timePeriod) {

        for (IPartnership p : getPartnerships()) {
            for (IPerson c : p.getChildren()) {
                if (DateUtils.dateBefore(currentDate.advanceTime(timePeriod), c.getBirthDate())) {
                    return false;
                }
            }
        }

        return true;

    }

    @Override
    public void recordPartnership(IPartnership partnership) {
        this.partnerships.add(partnership);
    }

    @Override
    public void recordDeath(Date date) {

        try {
            if (partnerships.size() != 0) {
                IPerson lastSpouse = getLastChild().getParentsPartnership().getPartnerOf(this);
                if (lastSpouse.aliveOnDate(date)) {
                    // if the partner is alive on date of death
                    if (lastSpouse.getLastChild().getParentsPartnership().getPartnerOf(lastSpouse).getId() == id) {
                        // and if the lastSpouses last partner is this person
                        // then this is the end of a partnership - caused by death
                        Simulation.pc.partnershipEnd();
                    }
                }
            }
        } catch (NullPointerException e ) {
            System.out.println("Null spouse in existing partnership?");
        }

        Simulation.pc.death(this);
        deathDate = date.getDateInstant();
    }

    @Override
    public void causeEventInTimePeriod(EventType event, Date latestDate, CompoundTimeUnit timePeriod) {

        if(isDeathEvent(event)) {
            int daysInTimePeriod = DateUtils.getDaysInTimePeriod(latestDate, timePeriod.negative());

            if(sex == 'm') {
                // No events to prevent death in last time period
                recordDeath(deathDateSelector.selectDate(latestDate, timePeriod));
            } else {
                // if female

                IPerson lastChild = getLastChild();

                if (lastChild == null) {
                    recordDeath(deathDateSelector.selectDate(latestDate, timePeriod));
                } else {

                    int daysSinceLastChild = DateUtils.differenceInDays(lastChild.getBirthDate(), latestDate);

                    // if last child was born in time period
                    if (daysSinceLastChild < daysInTimePeriod) {
                        // then restrict date selection
                        recordDeath(deathDateSelector.selectDate(latestDate, timePeriod, daysSinceLastChild));
                    } else {
                        // else apply death date as usual
                        recordDeath(deathDateSelector.selectDate(latestDate, timePeriod));
                    }
                }
            }

        }

    }

    private boolean isBirthEvent(EventType event) {
        return event == EventType.FIRST_BIRTH || event == EventType.SECOND_BIRTH || event == EventType.THIRD_BIRTH ||
                event == EventType.FOURTH_BIRTH || event == EventType.FIFTH_BIRTH;
    }

    private boolean isDeathEvent(EventType event) {
        return event == EventType.MALE_DEATH || event == EventType.FEMALE_DEATH;
    }

    @Override
    public int ageAtDeath() throws NotDeadException {
        if (deathDate == null) {
            throw new NotDeadException();
        }
        return DateUtils.differenceInYears(birthDate, deathDate).getCount();
    }

    @Override
    public boolean aliveOnDate(Date date) {

        if (DateUtils.dateBefore(birthDate, date)) {
            if (deathDate == null || DateUtils.dateBefore(date, deathDate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int ageAtFirstChild() throws NoChildrenOfDesiredOrder {

        int age = Integer.MAX_VALUE;

        for (IPartnership p : partnerships) {
            for (IPerson c : p.getChildren()) {
                int ageAtBirth = DateUtils.differenceInYears(birthDate, c.getBirthDate()).getCount();
                if (ageAtBirth < age) {
                    age = ageAtBirth;
                }
            }
        }

        if (age == Integer.MAX_VALUE) {
            throw new NoChildrenOfDesiredOrder("Women has no children");
        }

        return age;
    }

    @Override
    public IPerson getLastChild() {

        Date latestChildBirthDate = new YearDate(Integer.MIN_VALUE);
        IPerson child = null;

        for (IPartnership p : partnerships) {
            for (IPerson c : p.getChildren()) {

                if(DateUtils.dateBefore(latestChildBirthDate, c.getBirthDate())) {
                    latestChildBirthDate = c.getBirthDate();
                    child = c;
                }

            }
        }

        return child;

    }

    @Override
    public int numberOfChildren() {
        int count = 0;

        for(IPartnership p : partnerships) {
            count += p.getChildren().size();
        }

        return count;
    }

    @Override
    public void keepPreviousFatherForChild(IPartnership child, PeopleCollection population) throws PersonNotAliveException {

        IPerson lastChildWithFatherAssigned = getLastChildWithFatherAssigned();

        // This is the partnership with the last father - we're wanting to put any fatherless kids in here
        IPartnership mothersPrevPartnership = lastChildWithFatherAssigned.getParentsPartnership();

        // The child just born who we have decided (by virtue of calling this method) to fold into the mothers previous
        // partnership
        IPerson newChild = child.getChildren().get(0);

        // Checks that the father from the previous partnership was able to father the child(ren) that have just been born
        try {
            if(!mothersPrevPartnership.getMalePartner().aliveOnDate(newChild.getBirthDate().getDateClock(true).advanceTime(-9, TimeUnit.MONTH))) {
                throw new PersonNotAliveException("Trying to use a father from a previous marriage who is now dead");
            }
        } catch (UnsupportedDateConversion unsupportedDateConversion) {
            throw new Error(unsupportedDateConversion);
        }

        // This is the partnership from which we want to move any just born kids into the mothers previous partnership we found above
        IPartnership newChildrenPartnership = newChild.getParentsPartnership();

        if(newChildrenPartnership.getId() != child.getId()) {
            throw new Error("IPartnership given as method parameter is not consistent with the partnership found in" +
                    "the mothers partnership list");
        }

        Collection<IPerson> newChildren = newChildrenPartnership.getChildren();

        // Sets the parents partnership of all the newly born children to the mothers previous partnership
        for(IPerson c : newChildren) {
            c.setParentsPartnership(mothersPrevPartnership);
        }

        mothersPrevPartnership.addChildren(newChildren);
        partnerships.remove(newChildrenPartnership);
        population.removePartnershipFromIndex(newChildrenPartnership);


    }

    private IPerson getLastChildWithNoFatherAssigned() {

        IPerson lastChildWithNoFatherAssigned = null;
        Date latestChildBirthDate = new YearDate(Integer.MIN_VALUE);

        for (IPartnership p : partnerships) {
            for (IPerson c : p.getChildren()) {

                if(p.getMalePartner() == null && DateUtils.dateBefore(latestChildBirthDate, c.getBirthDate())) {
                    latestChildBirthDate = c.getBirthDate();
                    lastChildWithNoFatherAssigned = c;
                }

            }
        }
        return lastChildWithNoFatherAssigned;
    }

    public IPerson getLastChildWithFatherAssigned() {

        IPerson lastChildWithFatherAssigned = null;
        Date latestChildBirthDate = new YearDate(Integer.MIN_VALUE);

        for (IPartnership p : partnerships) {
            for (IPerson c : p.getChildren()) {

                if(p.getMalePartner() != null && DateUtils.dateBefore(latestChildBirthDate, c.getBirthDate())) {
                    latestChildBirthDate = c.getBirthDate();
                    lastChildWithFatherAssigned = c;
                }

            }
        }
        return lastChildWithFatherAssigned;
    }

    @Override
    public void setParentsPartnership(IPartnership newParents) {
        parentsPartnership = newParents;
    }

    @Override
    public int numberOfChildrenFatheredChildren() {
        int count = 0;

        for(IPartnership p : partnerships) {
            if(p.getMalePartner() != null) {
                count += p.getChildren().size();
            }
        }

        return count;
    }

    @Override
    public IPartnership isInstigatorOfSeparationOfMothersPreviousPartnership() {

        Collection<IPerson> fullSiblings = parentsPartnership.getChildren();
        fullSiblings.remove(this);


        // check to see if eldest sibling
        boolean eldest = true;
        for(IPerson sibling : fullSiblings) {
            if(DateUtils.dateBefore(sibling.getBirthDate(), getBirthDate())) {
                eldest = false;
            }
        }

        IPartnership prevPartnership = null;

        if(eldest) {
            // is first child of partnership then look to see if there is a previous partnership for the mother
            Collection<IPartnership> mothersPartnerships = parentsPartnership.getFemalePartner().getPartnerships();
            mothersPartnerships.remove(parentsPartnership);

            for(IPartnership p : mothersPartnerships) {

                if(DateUtils.dateBefore(p.getPartnershipDate(), parentsPartnership.getPartnershipDate())) {
                    if(prevPartnership != null) {
                        if(DateUtils.dateBefore(prevPartnership.getPartnershipDate(), p.getPartnershipDate())) {
                            prevPartnership = p;
                        }
                    } else {
                        prevPartnership = p;
                    }
                }

            }


        }

        fullSiblings.add(this);

        return prevPartnership;
    }

    @Override
    public boolean isWidow(Date onDate) {

        IPerson partner = getPartner(onDate);

        if(partner == null) {
            return false;
        } else {
            return !partner.aliveOnDate(onDate);
        }

    }

    @Override
    public IPerson getPartner(Date onDate) {

        IPartnership currentPartnership = null;

        for(IPartnership p : partnerships) {

            if(DateUtils.dateBefore(p.getPartnershipDate(), onDate)) {

                if(currentPartnership != null) {

                    if(DateUtils.dateBefore(currentPartnership.getPartnershipDate(), p.getPartnershipDate())) {
                        currentPartnership = p;
                    }

                } else {
                    currentPartnership = p;
                }

            }

        }

        if(currentPartnership == null) {
            return null;
        } else if(sex == MALE) {
            return currentPartnership.getFemalePartner();
        } else {
            return currentPartnership.getMalePartner();
        }

    }

    @Override
    public int ageOnDate(Date currentTime) {
        return DateUtils.differenceInYears(birthDate, currentTime).getCount();
    }

    public String toString() {

        String s = "";

        s += getId() + " ";
        s += getSex() + " ";

        s += birthDate != null ? birthDate.toString() : "null";
        s += " > ";
        s += deathDate != null ? deathDate.toString() : "null";
        s += " ";

        s += "Parents ID: ";
        s += parentsPartnership != null ? parentsPartnership.getId() : "null";
        s += " ";
        s += "Partnership IDs: {";

        for(int i = 0; i < partnerships.size(); i ++) {
            s += partnerships.get(i).getId();
            if(i != partnerships.size() - 1) {
                s += ", ";
            }
        }

        s += "} ";
        s += firstName + " " + surname;

        return s;

    }

}
