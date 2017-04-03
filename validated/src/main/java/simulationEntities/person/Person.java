package simulationEntities.person;

import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.MonthDate;
import dateModel.dateImplementations.ExactDate;
import dateModel.dateImplementations.YearDate;
import dateModel.dateSelection.BirthDateSelector;
import dateModel.dateSelection.DateSelector;
import dateModel.dateSelection.DeathDateSelector;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventType;
import events.birth.NoChildrenOfDesiredOrder;
import events.death.NotDeadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulationEntities.partnership.IPartnership;
import simulationEntities.population.PopulationCounts;
import simulationEntities.population.dataStructure.PeopleCollection;
import simulationEntities.population.dataStructure.Population;

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
    private ExactDate birthDate;
    private ExactDate deathDate;
    private List<IPartnership> partnerships = new ArrayList<>();
    private IPartnership parentsPartnership;
    private String firstName;
    private String surname;

    private DateSelector deathDateSelector = new DeathDateSelector();
    private DateSelector birthDateSelector = new BirthDateSelector();


    public Person(char sex, Date birthDate) {
        id = getNewId();
        this.sex = Character.toLowerCase(sex);
        this.birthDate = birthDate.getExactDate();
    }

    public Person(char sex, Date birthDate, IPartnership parentsPartnership) {
        id = getNewId();
        this.sex = Character.toLowerCase(sex);
        this.birthDate = birthDate.getExactDate();
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
    public Date getBirthDate() {
        return birthDate;
    }

    @Override
    public Date getDeathDate() {
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
    public boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod) {

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
    public boolean recordDeath(Date date, Population population) {

        if (partnerships.size() != 0) {
            IPerson lastSpouse = getLastChild().getParentsPartnership().getPartnerOf(this);
            if(lastSpouse == null) {
                System.out.println("A");
            }
            if (lastSpouse.aliveOnDate(date)) {
                // if the partner is alive on date of death
                if (lastSpouse.getLastChild().getParentsPartnership().getPartnerOf(lastSpouse).getId() == id) {
                    // and if the lastSpouses last partner is this person
                    // then this is the end of a partnership - caused by death
                    population.getPopulationCounts().partnershipEnd();
                }
            }
        }

        population.getPopulationCounts().death(this);

        deathDate = date.getExactDate();

        return true;
    }

    @Override
    public void causeEventInTimePeriod(EventType event, Date latestDate, CompoundTimeUnit timePeriod) {

        if(isDeathEvent(event)) {
            int daysInTimePeriod = DateUtils.getDaysInTimePeriod(latestDate, timePeriod.negative());

            if(sex == 'm') {
                // No events to prevent death in last time period
                deathDate = deathDateSelector.selectDate(latestDate, timePeriod);
            } else {
                // if female

                IPerson lastChild = getLastChild();

                if (lastChild == null) {
                    deathDate = deathDateSelector.selectDate(latestDate, timePeriod);
                } else {

                    int daysSinceLastChild = DateUtils.differenceInDays(lastChild.getBirthDate(), latestDate);

                    // if last child was born in time period
                    if (daysSinceLastChild < daysInTimePeriod) {
                        // then restrict date selection
                        deathDate = deathDateSelector.selectDate(latestDate, timePeriod, daysSinceLastChild);
                    } else {
                        // else apply death date as usual
                        deathDate = deathDateSelector.selectDate(latestDate, timePeriod);
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
    public void keepFather(PeopleCollection population) {
        Date latestChildBirthDate = new YearDate(Integer.MIN_VALUE);
        IPerson child = null;



        for (IPartnership p : partnerships) {
            for (IPerson c : p.getChildren()) {

                if(p.getMalePartner() != null && DateUtils.dateBefore(latestChildBirthDate, c.getBirthDate())) {
                    latestChildBirthDate = c.getBirthDate();
                    child = c;
                }

            }
        }

        // This is the partnership with the last father - we're wanting to put any fatherless kids in here
        IPartnership motherPrevChild = child.getParentsPartnership();


        IPerson newChild = getLastChild();
        IPartnership old = newChild.getParentsPartnership();

        Collection<IPerson> newChildren = old.getChildren();

        for(IPerson c : newChildren) {
            c.setParentsPartnership(motherPrevChild);

        }

        motherPrevChild.addChildren(newChildren);
        partnerships.remove(old);
        population.removePartnershipFromIndex(old);

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

}
