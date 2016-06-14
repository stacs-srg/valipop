package model;

import utils.time.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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
    private List<IPartnership> partnerships = new ArrayList<IPartnership>();
    private IPartnership parentsPartnership;
    private String firstName;
    private String surname;


    public Person(char sex, Date birthDate) {
        id = getNewId();
        this.sex = Character.toLowerCase(sex);
        this.birthDate = birthDate.getInstant();
    }

    public Person(char sex, Date birthDate, IPartnership parentsPartnership) {
        id = getNewId();
        this.sex = Character.toLowerCase(sex);
        this.birthDate = birthDate.getInstant();
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
    public boolean noRecentChildren(DateClock currentDate) {

        for (IPartnership p : getPartnerships()) {
            for (IPerson c : p.getChildren()) {
                if (DateUtils.dateBefore(currentDate.advanceTime(-9, TimeUnit.MONTH), c.getBirthDate())) {
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
        deathDate = date.getInstant();
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

}
