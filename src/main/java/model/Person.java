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

    public void recordPartnership(IPartnership partnership) {
        this.partnerships.add(partnership);
    }

    public void recordDeath(Date date) {
        deathDate = date.getInstant();
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
    public int compareTo(IPerson o) {
        return this.id == o.getId() ? 0 : -1;
    }

    public boolean noRecentChildren(DateClock currentDate) {

        for(IPartnership p : partnerships) {
           for(IPerson c : p.getChildren()) {
               if(DateUtils.dateBefore(currentDate.advanceTime(-9, TimeUnit.MONTH), c.getBirthDate())) {
                   return false;
               }
           }
        }

        return true;

    }
}
